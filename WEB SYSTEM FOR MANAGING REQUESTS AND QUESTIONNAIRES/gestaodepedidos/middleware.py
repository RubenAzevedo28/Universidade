from django.db import connections
from django.db.utils import OperationalError
from django.http import HttpResponse

class CheckXAMPPMiddleware:
    def __init__(self, get_response):
        self.get_response = get_response

    def __call__(self, request):
        db_conn = connections['default']
        try:
            db_conn.cursor()
        except OperationalError:
            return HttpResponse("Error: XAMPP is turned off or the database is not accessible.", status=503)
        
        response = self.get_response(request)
        return response
