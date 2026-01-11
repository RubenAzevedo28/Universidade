from django.db import models

class ano_letivo(models.Model):
    id = models.AutoField(db_column='ID', primary_key=True)  # Field name made lowercase.
    ano_letivo = models.CharField(db_column='ano_letivo', max_length=255)
    dia_inicio = models.DateField(blank=True, null=True)
    dia_fim = models.DateField(blank=True, null=True)
    ativo = models.CharField(db_column='ativo', max_length=10)
    questionario = models.ForeignKey('questionarios.Questionario', on_delete=models.CASCADE, db_column='questionario_id', related_name='q_ano_letivos')
   
    class Meta:
        db_table = 'ano_letivo'

    def __str__(self):
        return self.ano_letivo