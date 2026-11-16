"""
Questão 4 - Garbage Collection em Python
Sistemas Operacionais

Objetivo:
Demonstrar o funcionamento da coleta de lixo em Python,
utilizando contagem de referências e coleta geracional.
"""

import gc
import sys


# ============================================================
# CLASSE OBJETO
# ============================================================

class Objeto:
    """Classe que simula o consumo de memória."""

    def __init__(self, nome, tamanho=100_000):
        self.nome = nome

        # Cria uma lista grande para simular o uso de memória.
        self.dados = [0] * tamanho

        # Atributo utilizado para criar referências circulares.
        self.referencia = None

        print(f"[CRIADO] {self.nome}")

    def __del__(self):
        # Executado quando o objeto é destruído.
        print(f"[DESTRUIDO] {self.nome}")


# ============================================================
# FUNÇÃO PARA ORGANIZAR OS CENÁRIOS
# ============================================================

def titulo(texto):
    print("\n" + "=" * 65)
    print(texto)
    print("=" * 65)


# ============================================================
# CENÁRIO 1 - CONTAGEM DE REFERÊNCIAS
# ============================================================

def cenario_1():

    titulo("CENÁRIO 1 - CONTAGEM DE REFERÊNCIAS")

    # Cria um objeto e atribui uma referência a ele.
    a = Objeto("Objeto A")

    # Mostra a quantidade de referências.
    # getrefcount adiciona uma referência temporária durante a chamada.
    print(f"\nReferências iniciais: {sys.getrefcount(a)}")

    # Cria outra referência para o mesmo objeto.
    b = a
    print(f"Após b = a: {sys.getrefcount(a)}")

    # Armazena uma referência dentro de uma lista.
    lista = [a]
    print(f"Após adicionar à lista: {sys.getrefcount(a)}")

    # Remove uma das referências.
    del b
    print(f"Após del b: {sys.getrefcount(a)}")

    # Remove a referência armazenada na lista.
    lista.clear()
    print(f"Após limpar a lista: {sys.getrefcount(a)}")

    # Remove a última referência.
    print("\nRemovendo a última referência...")
    del a

    print("Objeto destruído automaticamente pela contagem de referências.")


# ============================================================
# CENÁRIO 2 - REFERÊNCIAS CIRCULARES
# ============================================================

def cenario_2():

    titulo("CENÁRIO 2 - REFERÊNCIAS CIRCULARES")

    # Cria dois objetos.
    x = Objeto("Objeto X")
    y = Objeto("Objeto Y")

    # Cria um ciclo:
    # X aponta para Y e Y aponta para X.
    x.referencia = y
    y.referencia = x

    print("\nReferências antes da remoção:")
    print(f"Objeto X: {sys.getrefcount(x)}")
    print(f"Objeto Y: {sys.getrefcount(y)}")

    # Remove as referências externas.
    del x, y

    print("\nReferências externas removidas.")
    print("Os objetos continuam ligados entre si pelo ciclo.")

    # O coletor identifica e remove os objetos inalcançáveis.
    print("\nExecutando gc.collect()...")

    coletados = gc.collect()

    print(f"Objetos coletados: {coletados}")


# ============================================================
# CENÁRIO 3 - AUTOREFERÊNCIA
# ============================================================

def cenario_3():

    titulo("CENÁRIO 3 - AUTOREFERÊNCIA")

    # Cria um objeto.
    z = Objeto("Objeto Z")

    # O objeto passa a referenciar a si mesmo.
    z.referencia = z

    print(f"\nReferências de Z: {sys.getrefcount(z)}")

    # Remove a referência externa.
    del z

    print("\nReferência externa removida.")
    print("O objeto continua apontando para si mesmo.")

    # Força a coleta de lixo.
    print("\nExecutando gc.collect()...")

    coletados = gc.collect()

    print(f"Objetos coletados: {coletados}")


# ============================================================
# CENÁRIO 4 - COLETA GERACIONAL
# ============================================================

def cenario_4():

    titulo("CENÁRIO 4 - COLETA GERACIONAL")

    # Mostra os limites utilizados pelo coletor.
    print(f"Limites das gerações: {gc.get_threshold()}")

    # Mostra os contadores atuais do coletor.
    print(f"Contadores atuais: {gc.get_count()}")

    # --------------------------------------------------------
    # PARTE A - OBJETOS SEM REFERÊNCIAS CIRCULARES
    # --------------------------------------------------------

    print("\nA) Criando objetos sem referências circulares:")

    for i in range(3):

        obj = Objeto(f"Simples {i}", tamanho=1000)

        # A contagem de referências permite a destruição imediata.
        del obj

    print("\nOs objetos simples foram destruídos automaticamente.")

    # --------------------------------------------------------
    # PARTE B - OBJETOS COM REFERÊNCIAS CIRCULARES
    # --------------------------------------------------------

    print("\nB) Criando objetos com referências circulares:")

    # Guarda o estado anterior do coletor.
    estado_gc = gc.isenabled()

    # Desabilita temporariamente a coleta automática.
    gc.disable()

    try:

        for i in range(3):

            p = Objeto(f"Ciclo {i}A", tamanho=1000)
            q = Objeto(f"Ciclo {i}B", tamanho=1000)

            # Cria referências circulares entre os objetos.
            p.referencia = q
            q.referencia = p

            # Remove as referências externas.
            del p, q

        print("\nContadores antes da coleta:")
        print(gc.get_count())

        print("\nOs ciclos continuam na memória até a coleta.")

        # ----------------------------------------------------
        # COLETA DA GERAÇÃO 0
        # ----------------------------------------------------

        print("\nC) Coletando a geração 0:")

        coletados_0 = gc.collect(0)

        print(f"Objetos coletados na geração 0: {coletados_0}")

        # ----------------------------------------------------
        # COLETA COMPLETA
        # ----------------------------------------------------

        print("\nD) Executando coleta completa:")

        coletados_total = gc.collect()

        print(f"Objetos coletados na coleta completa: {coletados_total}")

    finally:

        # Restaura o estado anterior do Garbage Collector.
        if estado_gc:
            gc.enable()
        else:
            gc.disable()


# ============================================================
# ESTATÍSTICAS DO GARBAGE COLLECTOR
# ============================================================

def estatisticas():

    titulo("ESTATÍSTICAS DO GARBAGE COLLECTOR")

    # Obtém as estatísticas de cada geração.
    dados = gc.get_stats()

    for geracao, info in enumerate(dados):

        print(f"\nGeração {geracao}")

        print(f"Coletas realizadas: {info['collections']}")

        print(f"Objetos coletados: {info['collected']}")

        print(f"Objetos não coletados: {info['uncollectable']}")


# ============================================================
# PROGRAMA PRINCIPAL
# ============================================================

def main():

    print("=" * 65)
    print(" DEMONSTRAÇÃO DO GARBAGE COLLECTION EM PYTHON")
    print("=" * 65)

    print(f"\nVersão do Python: {sys.version.split()[0]}")

    print(f"Garbage Collector habilitado: {gc.isenabled()}")

    try:

        # Executa os quatro cenários.
        cenario_1()

        cenario_2()

        cenario_3()

        cenario_4()

        # Apresenta as estatísticas finais.
        estatisticas()

    except MemoryError:

        print("\nErro: memória insuficiente para executar o programa.")

    finally:

        # Garante que o coletor permaneça habilitado ao finalizar.
        gc.enable()

        print("\n" + "=" * 65)
        print(" DEMONSTRAÇÃO FINALIZADA")
        print("=" * 65)


# Inicia o programa.
if __name__ == "__main__":
    main()
