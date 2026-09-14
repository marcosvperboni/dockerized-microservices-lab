from __future__ import annotations

import json
import logging
import threading
import time

import pika
from pika.exceptions import AMQPConnectionError

from app.application.notification_service import NotificationService
from app.config import Settings

logger = logging.getLogger(__name__)


class OrderCreatedConsumer:
    """Background consumer that reacts to order.created events published by order-service."""

    def __init__(self, settings: Settings, notification_service: NotificationService):
        self._settings = settings
        self._notification_service = notification_service
        self._thread: threading.Thread | None = None
        self._stop_event = threading.Event()

    def start(self) -> None:
        self._thread = threading.Thread(target=self._run_with_retry, name="order-created-consumer", daemon=True)
        self._thread.start()

    def stop(self) -> None:
        self._stop_event.set()

    def _run_with_retry(self) -> None:
        while not self._stop_event.is_set():
            try:
                self._consume()
            except AMQPConnectionError as exc:
                logger.warning("RabbitMQ connection failed (%s); retrying in 5s", exc)
                time.sleep(5)
            except Exception:
                logger.exception("Unexpected error in order.created consumer; retrying in 5s")
                time.sleep(5)

    def _consume(self) -> None:
        credentials = pika.PlainCredentials(self._settings.rabbitmq_user, self._settings.rabbitmq_password)
        parameters = pika.ConnectionParameters(
            host=self._settings.rabbitmq_host,
            port=self._settings.rabbitmq_port,
            credentials=credentials,
        )
        connection = pika.BlockingConnection(parameters)
        channel = connection.channel()
        channel.exchange_declare(exchange=self._settings.orders_exchange, exchange_type="topic", durable=True)
        channel.queue_declare(queue=self._settings.notification_queue, durable=True)
        channel.queue_bind(
            queue=self._settings.notification_queue,
            exchange=self._settings.orders_exchange,
            routing_key=self._settings.order_created_routing_key,
        )

        def on_message(ch, method, _properties, body):
            try:
                event = json.loads(body)
                self._notification_service.register_order_created(
                    order_id=event["orderId"],
                    customer_name=event["customerName"],
                    product_name=event["productName"],
                    quantity=event["quantity"],
                )
                ch.basic_ack(delivery_tag=method.delivery_tag)
            except Exception:
                logger.exception("Failed to process order.created message; rejecting without requeue")
                ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)

        channel.basic_consume(queue=self._settings.notification_queue, on_message_callback=on_message)
        logger.info("Listening for order.created events on queue '%s'", self._settings.notification_queue)

        try:
            while not self._stop_event.is_set():
                connection.process_data_events(time_limit=1)
        finally:
            if connection.is_open:
                connection.close()
