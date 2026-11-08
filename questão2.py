# O programa simula um gerenciador de memória com partições fixas,
# utilizando o algoritmo First-Fit para alocar processos.

# Lista que representa as partições fixas de memória.
# Cada partição possui tamanho, processo alocado e fragmentação interna.

particoes = [
    {"tamanho": 100, "processo": None, "fragmentacao": 0},
    {"tamanho": 150, "processo": None, "fragmentacao": 0},
    {"tamanho": 200, "processo": None, "fragmentacao": 0},
    {"tamanho": 250, "processo": None, "fragmentacao": 0},
    {"tamanho": 300, "processo": None, "fragmentacao": 0}
]


def alocar_processo(nome, tamanho):
    """Aloca um processo na primeira partição livre compatível."""

    # Valida os dados recebidos antes de tentar alocar.
    if not isinstance(nome, str) or not nome.strip():
        print("Erro: o nome do processo deve ser válido.")
        return False

    if isinstance(tamanho, bool) or not isinstance(tamanho, int):
        print("Erro: o tamanho deve ser um número inteiro.")
        return False

    if tamanho <= 0:
        print("Erro: o tamanho deve ser maior que zero.")
        return False

    # Impede que um mesmo processo seja alocado duas vezes.
    for particao in particoes:
        processo = particao["processo"]

        if processo is not None and processo == nome:
            print(f"Erro: o processo {nome} já está alocado.")
            return False

    # First-Fit: percorre as partições na ordem em que aparecem
    # e seleciona a primeira livre que comporte o processo.
    for indice, particao in enumerate(particoes):
        if particao["processo"] is None and particao["tamanho"] >= tamanho:

            # Registra o processo na partição escolhida.
            particao["processo"] = nome

            # Calcula o espaço desperdiçado dentro da partição.
            particao["fragmentacao"] = particao["tamanho"] - tamanho

            print(f"\nProcesso {nome} alocado com sucesso.")
            print(f"Partição utilizada: {indice + 1}")
            print(f"Tamanho do processo: {tamanho} unidades")
            print(f"Tamanho da partição: {particao['tamanho']} unidades")
            print(
                "Fragmentação interna: "
                f"{particao['fragmentacao']} unidades"
            )

            return True

    # Executado quando nenhuma partição livre é suficiente.
    print(
        f"\nFalha na alocação de {nome}: "
        "não existe partição livre compatível."
    )
    return False


def liberar_processo(nome):
    """Libera a partição ocupada pelo processo informado."""

    for indice, particao in enumerate(particoes):
        if particao["processo"] == nome:

            # Remove o processo e zera sua fragmentação interna.
            particao["processo"] = None
            particao["fragmentacao"] = 0

            print(
                f"\nProcesso {nome} liberado da partição "
                f"{indice + 1}."
            )
            return True

    # Informa quando o processo não está alocado.
    print(f"\nErro: processo {nome} não encontrado na memória.")
    return False


def exibir_memoria():
    """Mostra a situação atual de todas as partições."""

    print("\n========== ESTADO DA MEMÓRIA ==========")

    for indice, particao in enumerate(particoes):
        processo = particao["processo"]

        if processo is None:
            estado = "Livre"
            fragmentacao = 0
        else:
            estado = f"Ocupada por {processo}"
            fragmentacao = particao["fragmentacao"]

        print(
            f"Partição {indice + 1} | "
            f"Tamanho: {particao['tamanho']} | "
            f"Estado: {estado} | "
            f"Fragmentação interna: {fragmentacao}"
        )

    print("=======================================")


def calcular_fragmentacao_total():
    """Soma a fragmentação interna dos processos atualmente alocados."""

    total = sum(
        particao["fragmentacao"]
        for particao in particoes
        if particao["processo"] is not None
    )

    print(f"\nFragmentação interna total: {total} unidades")
    return total


def executar_simulacao():
    """Executa a sequência de testes especificada no enunciado."""

    print("SIMULAÇÃO DE FRAGMENTAÇÃO DE MEMÓRIA")
    print("Memória total: 1000 unidades")
    print("Algoritmo de alocação: First-Fit")

    # 1. Aloca P1 com 90 unidades.
    alocar_processo("P1", 90)
    exibir_memoria()

    # 2. Aloca P2 com 140 unidades.
    alocar_processo("P2", 140)
    exibir_memoria()

    # 3. Aloca P3 com 180 unidades.
    alocar_processo("P3", 180)
    exibir_memoria()

    # 4. Libera a partição ocupada por P2.
    liberar_processo("P2")
    exibir_memoria()

    # 5. Aloca P4 com 100 unidades.
    alocar_processo("P4", 100)
    exibir_memoria()

    # 6. Tenta alocar P5 com 350 unidades.
    # A alocação deve falhar, pois nenhuma partição possui
    # tamanho suficiente para acomodar esse processo.
    alocar_processo("P5", 350)
    exibir_memoria()

    # 7. Calcula a fragmentação interna dos processos alocados.
    calcular_fragmentacao_total()


# Inicia a simulação quando o arquivo é executado diretamente.
if __name__ == "__main__":
    executar_simulacao()
