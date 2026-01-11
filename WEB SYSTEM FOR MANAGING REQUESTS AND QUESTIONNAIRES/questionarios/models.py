from django.db import models
from anoletivo.models import ano_letivo
from main.models import AuthUser

class EstadoQuestionario(models.Model):
    estado = models.CharField(max_length=255)
    descricao = models.TextField(blank=True, null=True)
    class Meta:
        db_table = 'estado_questionario'
    def __str__(self):
        return self.estado
    
class TemaPergunta(models.Model):
    tema = models.CharField(max_length=255)
    descricao = models.TextField(blank=True, null=True)
    class Meta:
        db_table = 'tema_pergunta'
    def __str__(self):
        return self.tema


class Questionario(models.Model):
    descricao = models.CharField(max_length=255)
    assunto = models.CharField(max_length=255)
    estado = models.ForeignKey(EstadoQuestionario, on_delete=models.CASCADE)
    data_criacao = models.DateField()
    data_submissao = models.DateField()
    data_validacao = models.DateField()
    comentarios = models.CharField(db_column='comentarios', max_length=255, blank=True, null=True)

    class Meta:
        db_table = 'questionario'


class TipoPergunta(models.Model):
    descricao = models.CharField(max_length=255)

    class Meta:
        db_table = 'tipo_pergunta'

    def __str__(self):
        return self.descricao
    
class Pergunta(models.Model):
    conteudo = models.CharField(max_length=255)
    questionario = models.ForeignKey(Questionario, on_delete=models.CASCADE)
    tipo_pergunta = models.ForeignKey(TipoPergunta, on_delete=models.CASCADE)
    tema = models.ForeignKey(TemaPergunta, on_delete=models.CASCADE)
    valor_minimo = models.IntegerField(null=True, blank=True)
    valor_maximo = models.IntegerField(null=True, blank=True)
    significado_minimo = models.CharField(max_length=255, null=True, blank=True)
    significado_maximo = models.CharField(max_length=255, null=True, blank=True)
    ano_letivo = models.ForeignKey(ano_letivo, on_delete=models.SET_NULL, null=True, blank=True)

    class Meta:
        db_table = 'pergunta'



class Resposta(models.Model):
    conteudo_int = models.IntegerField(null=True, blank=True)
    conteudo_string = models.CharField(max_length=255, null=True, blank=True)
    pergunta = models.ForeignKey(Pergunta, on_delete=models.CASCADE)

    class Meta:
        db_table = 'resposta'


class UtilizadorQuestionario(models.Model):
    utilizadoruser_ptr_id = models.IntegerField()
    questionario = models.ForeignKey(Questionario, on_delete=models.CASCADE)

    class Meta:
        db_table = 'utilizador_questionario'
        unique_together = (('utilizadoruser_ptr_id', 'questionario'),)

    def __str__(self):
        return str(self.utilizadoruser_ptr_id)

class OpcaoEscolhaMultipla(models.Model):
    pergunta = models.ForeignKey(Pergunta, related_name='opcoes', on_delete=models.CASCADE)
    texto_opcao = models.CharField(max_length=255)

    class Meta:
        db_table = 'opcao_escolha_multipla'

    def __str__(self):
        return self.texto_opcao
    
class RespostaUsuario(models.Model):
    user = models.ForeignKey(AuthUser, on_delete=models.CASCADE)
    questionario = models.ForeignKey(Questionario, on_delete=models.CASCADE)

    class Meta:
        db_table = 'resposta_usuario'
        unique_together = (('user', 'questionario'),)

    def __str__(self):
        return f"{self.user.username} - {self.questionario.assunto}"
