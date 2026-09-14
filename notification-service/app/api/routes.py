from __future__ import annotations

from fastapi import APIRouter, HTTPException, Request

from app.application.notification_service import NotificationNotFoundError, NotificationService
from app.domain.notification import Notification

router = APIRouter(prefix="/api/notifications", tags=["notifications"])


def _service(request: Request) -> NotificationService:
    return request.app.state.notification_service


@router.get("", response_model=list[Notification])
def list_notifications(request: Request) -> list[Notification]:
    return _service(request).list_all()


@router.get("/{notification_id}", response_model=Notification)
def get_notification(notification_id: int, request: Request) -> Notification:
    try:
        return _service(request).get_by_id(notification_id)
    except NotificationNotFoundError as exc:
        raise HTTPException(status_code=404, detail=str(exc)) from exc


health_router = APIRouter()


@health_router.get("/health")
def health() -> dict[str, str]:
    return {"status": "UP"}
