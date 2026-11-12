import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

/**
 * Questão Bônus - Simulação do algoritmo de substituição de páginas LRU
 * (Least Recently Used) e comparação com o FIFO da Questão 3.
 *
 * Compilação: javac SimuladorLRU.java
 * Execução:   java SimuladorLRU
 *
 * Entrada (a mesma da Questão 3):
 *   - Número de frames disponíveis
 *   - Sequência de referências separadas por vírgula ou espaço
 *     (ex: 7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2)
 *     Se a linha for deixada em branco, usa-se a sequência de exemplo.
 *
 * Lógica do LRU:
 *   Cada página guardada na memória tem um "timestamp" (instante da sua última
 *   referência). Esse registro fica em um Map<página, timestamp>.
 *   - Page Hit : a página já está nos frames -> só atualiza o timestamp dela
 *                (diferente do FIFO, o acerto "rejuvenesce" a página).
 *   - Page Fault com frame livre : carrega a página e registra o timestamp.
 *   - Page Fault com memória cheia : remove a página de MENOR timestamp
 *                (a menos recentemente usada) e carrega a nova.
 */
public class SimuladorLRU {

    private static final String SEQUENCIA_PADRAO = "7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // ---------- 1. Leitura e validação da entrada ----------
        int numFrames = lerNumeroFrames(sc);
        int[] referencias = lerReferencias(sc);

        // ---------- 2. Simulação detalhada do LRU ----------
        int faltasLRU = simularLRU(numFrames, referencias, true);

        // ---------- 3. Simulação do FIFO (sem imprimir passo a passo) ----------
        int faltasFIFO = simularFIFO(numFrames, referencias);

        // ---------- 4. Comparação e análise ----------
        compararAlgoritmos(numFrames, referencias, faltasFIFO, faltasLRU);

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
     * Simula o LRU e devolve o total de page faults.
     *
     * @param imprimir true para mostrar o estado dos frames a cada referência
     */
    private static int simularLRU(int numFrames, int[] referencias, boolean imprimir) {
        // Frames: lista com as páginas atualmente na memória (posição = frame)
        List<Integer> frames = new ArrayList<>();
        // Registro do tempo de uso: página -> instante da última referência
        Map<Integer, Integer> ultimoUso = new HashMap<>();
        int faltas = 0;

        if (imprimir) {
            System.out.println("\n===== Simulação LRU | Frames: " + numFrames
                    + " | Referências: " + referencias.length + " =====\n");
        }

        for (int instante = 0; instante < referencias.length; instante++) {
            int pagina = referencias[instante];
            String status;

            if (ultimoUso.containsKey(pagina)) {
                // Acerto: a página já está na memória -> atualiza o timestamp
                status = "Page Hit";
            } else {
                faltas++;
                if (frames.size() < numFrames) {
                    // Ainda há frame livre: apenas carrega a página
                    frames.add(pagina);
                    status = "Page Fault";
                } else {
                    // Memória cheia: procura a página com o MENOR timestamp
                    int vitima = frames.get(0);
                    for (int p : frames) {
                        if (ultimoUso.get(p) < ultimoUso.get(vitima)) {
                            vitima = p;
                        }
                    }
                    // A nova página ocupa o frame da vítima
                    frames.set(frames.indexOf(vitima), pagina);
                    ultimoUso.remove(vitima);
                    status = "Page Fault (substituiu " + vitima + ")";
                }
            }
            // Em acerto ou falta, a página acabou de ser usada
            ultimoUso.put(pagina, instante);

            if (imprimir) {
                System.out.printf("Referência: %d | Frames: %s | %s | Total Faults: %d%n",
                        pagina, frames, status, faltas);
            }
        }

        if (imprimir) {
            int total = referencias.length;
            int acertos = total - faltas;
            double taxaFaltas = (double) faltas / total;
            double taxaAcertos = (double) acertos / total;
            System.out.println("\n===== Estatísticas LRU =====");
            System.out.println("Total de referências : " + total);
            System.out.println("Total de page faults : " + faltas);
            System.out.println("Total de page hits   : " + acertos);
            System.out.printf("Taxa de faltas       : %d/%d = %.4f (%.2f%%)%n",
                    faltas, total, taxaFaltas, taxaFaltas * 100);
            System.out.printf("Taxa de acertos      : %.4f (%.2f%%)%n",
                    taxaAcertos, taxaAcertos * 100);
        }
        return faltas;
    }

    /**
     * Simula o FIFO (mesma lógica da Questão 3) e devolve o total de page faults.
     * Aqui ele serve apenas de base de comparação, por isso não imprime o passo a passo.
     */
    private static int simularFIFO(int numFrames, int[] referencias) {
        Queue<Integer> frames = new LinkedList<>();
        int faltas = 0;
        for (int pagina : referencias) {
            if (!frames.contains(pagina)) {          // hit não altera a fila
                faltas++;
                if (frames.size() == numFrames) {
                    frames.poll();                   // remove a página mais antiga
                }
                frames.add(pagina);
            }
        }
        return faltas;
    }

    /** Compara FIFO x LRU para o número de frames informado. */
    private static void compararAlgoritmos(int numFrames, int[] referencias,
                                           int faltasFIFO, int faltasLRU) {
        int total = referencias.length;
        double taxaFIFO = (double) faltasFIFO / total;
        double taxaLRU = (double) faltasLRU / total;

        System.out.println("\n===== Comparação FIFO x LRU (" + numFrames + " frames) =====");
        System.out.printf("%-10s | %-12s | %-12s | %-8s%n", "Algoritmo", "Page Faults", "Taxa Faltas", "Acertos");
        System.out.printf("%-10s | %-12d | %-12s | %-8d%n", "FIFO", faltasFIFO,
                String.format("%.2f%%", taxaFIFO * 100), total - faltasFIFO);
        System.out.printf("%-10s | %-12d | %-12s | %-8d%n", "LRU", faltasLRU,
                String.format("%.2f%%", taxaLRU * 100), total - faltasLRU);

        System.out.println("\n===== Análise comparativa =====");
        if (faltasLRU < faltasFIFO) {
            System.out.printf("O LRU teve %d falta(s) a menos que o FIFO (redução de %.2f pontos percentuais).%n",
                    faltasFIFO - faltasLRU, (taxaFIFO - taxaLRU) * 100);
            System.out.println("Motivo: o LRU mantém na memória as páginas usadas recentemente, "
                    + "aproveitando a localidade temporal das referências.");
        } else if (faltasLRU > faltasFIFO) {
            System.out.printf("O FIFO teve %d falta(s) a menos que o LRU nesta sequência.%n",
                    faltasLRU - faltasFIFO);
            System.out.println("Isso é possível em sequências específicas, pois nenhum algoritmo "
                    + "prático é ótimo para qualquer entrada.");
        } else {
            System.out.println("Os dois algoritmos tiveram o mesmo número de faltas nesta sequência.");
            System.out.println("Isso ocorre quando há pouca reutilização de páginas ou memória "
                    + "suficiente para todas elas.");
        }
    }
}
