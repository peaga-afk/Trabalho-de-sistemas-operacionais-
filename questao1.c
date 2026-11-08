/*
 * ============================================================================
 * Disciplina : Sistemas Operacionais
 * Trabalho   : Gerenciamento de Memória em Sistemas Operacionais
 * Questão 1  : Alocação estática vs. dinâmica (Parte B - Prática)
 * ============================================================================
 *
 * O que o programa faz:
 *   1. Declara um array ESTÁTICO de 5 inteiros e o preenche com 1..5
 *   2. Aloca DINAMICAMENTE (malloc) um array de 10 inteiros
 *   3. Preenche o array dinâmico com 10..19
 *   4. Imprime valores e endereços de memória de ambos os arrays
 *   5. Calcula a diferença entre os endereços (prova de que estão em áreas
 *      diferentes da memória)
 *   6. Libera a memória alocada dinamicamente (free)
 *
 * Como compilar:
 *   gcc -Wall -Wextra -std=c11 -o questao1 questao1.c
 *
 * Como executar:
 *   Linux / macOS : ./questao1
 *   Windows (MinGW): questao1.exe
 * ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>   /* malloc, free, EXIT_SUCCESS, EXIT_FAILURE */
#include <stdint.h>   /* uintptr_t: inteiro grande o bastante para guardar um endereço */

#define TAM_ESTATICO 5
#define TAM_DINAMICO 10

/*
 * Imprime cada posição de um vetor com seu valor e seu endereço.
 * Os endereços das posições são consecutivos, separados por sizeof(int) bytes.
 */
static void imprimir_vetor(const char *nome, const int *vetor, size_t tamanho)
{
    for (size_t i = 0; i < tamanho; i++) {
        printf("  %s[%zu] = %2d   | endereço: %p\n", nome, i, vetor[i], (const void *)&vetor[i]);
    }
}

int main(void)
{
    /* ------------------------------------------------------------------
     * 1. ALOCAÇÃO ESTÁTICA
     * O tamanho (5) é conhecido em tempo de compilação. A palavra 'static'
     * faz o array viver na área de dados do programa (.data/.bss), existindo
     * durante toda a execução. Não é preciso (nem possível) liberá-lo.
     * ------------------------------------------------------------------ */
    static int estatico[TAM_ESTATICO];

    for (int i = 0; i < TAM_ESTATICO; i++) {
        estatico[i] = i + 1;                       /* valores de 1 a 5 */
    }

    /* ------------------------------------------------------------------
     * 2. ALOCAÇÃO DINÂMICA
     * malloc reserva memória em tempo de EXECUÇÃO, no heap. O tamanho em
     * bytes é (quantidade de elementos) * sizeof(int). Usar sizeof(*ponteiro)
     * evita erro caso o tipo do ponteiro mude no futuro.
     * ------------------------------------------------------------------ */
    int *dinamico = malloc(TAM_DINAMICO * sizeof(*dinamico));

    /* malloc devolve NULL quando não há memória disponível. Usar o ponteiro
     * sem essa verificação causaria falha de segmentação. */
    if (dinamico == NULL) {
        fprintf(stderr, "Erro: falha ao alocar %zu bytes com malloc.\n",
                TAM_DINAMICO * sizeof(*dinamico));
        return EXIT_FAILURE;
    }

    /* 3. Preenche o array dinâmico com valores de 10 a 19 */
    for (int i = 0; i < TAM_DINAMICO; i++) {
        dinamico[i] = 10 + i;
    }

    /* ------------------------------------------------------------------
     * 4. VALORES E ENDEREÇOS
     * ------------------------------------------------------------------ */
    printf("=== Array ESTÁTICO (%d inteiros, área de dados) ===\n", TAM_ESTATICO);
    imprimir_vetor("estatico", estatico, TAM_ESTATICO);

    printf("\n=== Array DINÂMICO (%d inteiros, heap) ===\n", TAM_DINAMICO);
    imprimir_vetor("dinamico", dinamico, TAM_DINAMICO);

    printf("\n=== Endereços-base ===\n");
    printf("  Início do array estático : %p\n", (void *)estatico);
    printf("  Início do array dinâmico : %p\n", (void *)dinamico);

    /* ------------------------------------------------------------------
     * 5. DIFERENÇA ENTRE OS ENDEREÇOS
     * Subtrair ponteiros de objetos diferentes não é definido pela
     * linguagem C. Por isso convertemos os endereços para inteiros
     * (uintptr_t) antes de calcular a distância.
     * ------------------------------------------------------------------ */
    uintptr_t end_estatico = (uintptr_t)(void *)estatico;
    uintptr_t end_dinamico = (uintptr_t)(void *)dinamico;

    unsigned long long diferenca = (end_dinamico >= end_estatico)
            ? (unsigned long long)(end_dinamico - end_estatico)
            : (unsigned long long)(end_estatico - end_dinamico);

    printf("\n=== Diferença entre os endereços ===\n");
    printf("  |dinâmico - estático| = %llu bytes (0x%llx)\n", diferenca, diferenca);
    printf("  O heap está %s da área de dados do programa.\n",
           (end_dinamico >= end_estatico) ? "ACIMA (endereço maior)" : "ABAIXO (endereço menor)");
    printf("  Os dois arrays ocupam regiões diferentes da memória: o estático\n"
           "  fica na área de dados e o dinâmico no heap.\n");
    printf("  (O valor exato muda a cada execução por causa do ASLR do sistema.)\n");

    /* ------------------------------------------------------------------
     * 6. LIBERAÇÃO DA MEMÓRIA DINÂMICA
     * Todo malloc precisa de um free correspondente, senão ocorre
     * vazamento de memória (memory leak). Depois do free, o ponteiro passa
     * a apontar para memória inválida; atribuir NULL evita o uso acidental
     * (dangling pointer) e torna um segundo free() inofensivo.
     * ------------------------------------------------------------------ */
    free(dinamico);
    dinamico = NULL;
    printf("\nMemória dinâmica liberada com free(). Ponteiro redefinido para NULL.\n");

    return EXIT_SUCCESS;
}
