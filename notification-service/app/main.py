from __future__ import annotations

from contextlib import asynccontextmanager

from fastapi import FastAPI
from redis import Redis

from app.api.routes import health_router, router
from app.application.notification_service import NotificationService
from app.config import settings
from app.infrastructure.notification_repository import RedisNotificationRepository
from app.infrastructure.rabbitmq_consumer import OrderCreatedConsumer


@asynccontextmanager
async def lifespan(app: FastAPI):
    redis_client = Redis(host=settings.redis_host, port=settings.redis_port, decode_responses=True)
    repository = RedisNotificationRepository(redis_client)
    notification_service = NotificationService(repository)
    app.state.notification_service = notification_service

    consumer = OrderCreatedConsumer(settings, notification_service)
    consumer.start()

    yield

    consumer.stop()
    redis_client.close()


def create_app() -> FastAPI:
    app = FastAPI(title="notification-service", lifespan=lifespan)
    app.include_router(router)
    app.include_router(health_router)
    return app


app = create_app()
