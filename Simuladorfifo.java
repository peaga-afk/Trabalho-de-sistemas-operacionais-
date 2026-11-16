import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * Questão 3 - Simulação do algoritmo de substituição de páginas FIFO
 * (First-In, First-Out).
 *
 * Compilação: javac SimuladorFIFO.java
 * Execução:   java SimuladorFIFO
 *
 * Entrada:
 *   - Número de frames disponíveis
 *   - Sequência de referências separadas por vírgula ou espaço
 *     (ex: 7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2)
 *     Se a linha for deixada em branco, usa-se a sequência de exemplo.
 */
public class Simuladorfifo {

    private static final String SEQUENCIA_PADRAO = "7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // ---------- 1. Leitura e validação da entrada ----------
        int numFrames = lerNumeroFrames(sc);
        int[] referencias = lerReferencias(sc);

        // ---------- 2. Simulação ----------
        simularFIFO(numFrames, referencias);

        sc.close();
    }

    /** Lê o número de frames, repetindo até receber um inteiro positivo. */
    private static int lerNumeroFrames(Scanner sc) {
        while (true) {
            System.out.print("Informe o número de frames: ");
            String linha = sc.nextLine().trim();
            try {
                int n = Integer.parseInt(linha);
                if (n > 0) {
                    return n;
                }
                System.out.println("Erro: o número de frames deve ser maior que zero.");
            } catch (NumberFormatException e) {
                System.out.println("Erro: digite um número inteiro válido.");
            }
        }
    }

    /** Lê a sequência de referências, repetindo até receber uma entrada válida. */
    private static int[] lerReferencias(Scanner sc) {
        while (true) {
            System.out.print("Informe a sequência de referências (separadas por vírgula/espaço)\n"
                    + "[Enter = exemplo: " + SEQUENCIA_PADRAO + "]: ");
            String linha = sc.nextLine().trim();
            if (linha.isEmpty()) {
                linha = SEQUENCIA_PADRAO;
            }

            // Aceita vírgulas e/ou espaços como separadores
            String[] partes = linha.split("[,\\s]+");
            int[] refs = new int[partes.length];
            try {
                for (int i = 0; i < partes.length; i++) {
                    refs[i] = Integer.parseInt(partes[i]);
                }
                return refs;
            } catch (NumberFormatException e) {
                System.out.println("Erro: a sequência deve conter apenas números inteiros.");
            }
        }
    }

    /**
     * Simula o FIFO.
     * A Queue guarda as páginas na ordem de chegada
     * é sempre a página mais antiga, ou seja, a próxima a ser substituída.
     */
    private static void simularFIFO(int numFrames, int[] referencias) {
        Queue<Integer> frames = new LinkedList<>();
        int faltas = 0;

        System.out.println("\n===== Simulação FIFO | Frames: " + numFrames
                + " | Referências: " + referencias.length + " =====\n");

        for (int pagina : referencias) {
            String status;

            if (frames.contains(pagina)) {
                // Página já está na memória: A fila NÃO é alterada
                status = "Page Hit";
            } else {
                // Falta de página 
                faltas++;

                if (frames.size() < numFrames) {
                    // Ainda há frame livre: apenas carrega a página
                    frames.add(pagina);
                    status = "Page Fault";
                } else {
                    // Memória cheia: remove a página mais antiga 
                    int removida = frames.poll();
                    frames.add(pagina);
                    status = "Page Fault (substituiu " + removida + ")";
                }
            }

            System.out.printf("Referência: %d | Frames: %s | %s | Total Faults: %d%n",
                    pagina, frames, status, faltas);
        }

        // ---------- Estatísticas finais ----------
        int total = referencias.length;
        int acertos = total - faltas;
        double taxaFaltas = (double) faltas / total;
        double taxaAcertos = (double) acertos / total;

        System.out.println("\n===== Estatísticas =====");
        System.out.println("Total de referências : " + total);
        System.out.println("Total de page faults : " + faltas);
        System.out.println("Total de page hits   : " + acertos);
        System.out.printf("Taxa de faltas       : %d/%d = %.4f (%.2f%%)%n",
                faltas, total, taxaFaltas, taxaFaltas * 100);
        System.out.printf("Taxa de acertos      : %.4f (%.2f%%)%n",
                taxaAcertos, taxaAcertos * 100);
    }
}