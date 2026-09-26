from fastapi import FastAPI
from .router.excel_router import router as exr
from .router.query_router import router as qrr
# from .router.user_router import router as usr
# from .router.auth_router import router as aur
from .db.database import Base, engine
import py_eureka_client.eureka_client as eureka_client

app = FastAPI(
    title="YellowDoc.ai",
    version="1.0.0",
    summary="""
    Enterprise AI platform that transform invoices, receipts, tax documents, 
    and financial records into structured intelligence.
    """
)


Base.metadata.create_all(bind=engine)


app.include_router(exr)
app.include_router(qrr)
# app.include_router(usr)
# app.include_router(aur)

@app.on_event("startup")
async def register_with_eureka():
    await eureka_client.init_async(
        eureka_server="http://localhost:8761/eureka",
        app_name="YellowDoc",
        instance_port=8000,
    )

@app.get("/")
def home():
    return {
        "API" : {
            "application" : "YellowDoc.ai",
            "version" : "1.0.0"
        }
    }