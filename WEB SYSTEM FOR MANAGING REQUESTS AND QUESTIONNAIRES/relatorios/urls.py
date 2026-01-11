from django.urls import path
from .views import criar_relatorio_docentes, criar_relatorio_salas, criar_relatorio_pedidos, exportar_docentes_csv, exportar_docentes_pdf, exportar_pedidos_csv, exportar_pedidos_pdf

app_name = "relatorios"

urlpatterns = [
    path("criar_relatorio_salas", criar_relatorio_salas, name="criar-relatorio-salas"),
    path("criar_relatorio_pedidos", criar_relatorio_pedidos, name="criar-relatorio-pedidos"),
    path("criar_relatorio_docentes", criar_relatorio_docentes, name="criar-relatorio-docentes"),
    path("exportar_pedidos_csv", exportar_pedidos_csv, name="exportar-pedidos-csv"),
    path("exportar_pedidos_pdf", exportar_pedidos_pdf, name="exportar-pedidos-pdf"),
    path('relatorio/docentes/exportar_pdf/', exportar_docentes_pdf, name='exportar-docentes-pdf'),
    path('relatorio/docentes/exportar_csv/', exportar_docentes_csv, name='exportar-docentes-csv'),
]
