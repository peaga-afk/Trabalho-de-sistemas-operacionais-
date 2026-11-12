public class ComparacaoMemoria {
 
    static final int QUANTIDADE = 1_000_000;
    static final int REPETICOES = 5;
    static final int AQUECIMENTO = 3;
 
    // Campos estáticos usados para "consumir" o resultado de cada teste.
    // Sem isso, o JIT pode concluir que o trabalho é inútil e eliminá-lo.
    static long sumario;
    static Object destino;
 
    // Teste de alocação na stack (variáveis locais).
    // A JVM gerencia automaticamente as variáveis locais dos métodos.
    // A soma é gravada em um campo estático para que o loop não seja descartado.
    static void testeStack() {
        long soma = 0;
        for (int i = 0; i < QUANTIDADE; i++) {
            int numero = i;
            soma += numero;
        }
        sumario = soma;
    }
 
    // Teste de alocação no heap (objetos).
    // Cada objeto é criado e guardado em um campo estático, o que o faz
    // "escapar" do método e impede o JIT de evitar a alocação.
    // O objeto anterior perde a referência e pode ser coletado pelo Garbage Collector.
    // Integer.valueOf substitui new Integer (depreciado); para valores fora de
    // -128..127 ele cria um novo objeto no heap.
    static void testeHeap() {
        for (int i = 0; i < QUANTIDADE; i++) {
            destino = Integer.valueOf(i);
        }
    }
 
    public static void main(String[] args) {
 
        long tempoStack = 0;
        long tempoHeap = 0;
 
        // Aquecimento da JVM para reduzir variações iniciais.
        // Essas execuções não entram na média final.
        for (int i = 0; i < AQUECIMENTO; i++) {
            testeStack();
            testeHeap();
        }
 
        for (int i = 0; i < REPETICOES; i++) {
 
            long inicioStack = System.nanoTime();
            testeStack();
            long fimStack = System.nanoTime();
 
            tempoStack += fimStack - inicioStack;
 
            long inicioHeap = System.nanoTime();
            testeHeap();
            long fimHeap = System.nanoTime();
 
            tempoHeap += fimHeap - inicioHeap;
        }
 
        // Média por repetição, convertida de nanossegundos para milissegundos.
        double mediaStack = (tempoStack / (double) REPETICOES) / 1_000_000;
        double mediaHeap = (tempoHeap / (double) REPETICOES) / 1_000_000;
 
        System.out.printf("Média Stack: %.3f ms%n", mediaStack);
        System.out.printf("Média Heap: %.3f ms%n", mediaHeap);
 
        // O if evita divisão por zero.
        if (mediaStack > 0) {
            double diferenca = ((mediaHeap - mediaStack) / mediaStack) * 100;
            System.out.printf("Diferença percentual: %,.2f%%%n", diferenca);
        }
 
        // O heap pode ser mais lento devido à criação de objetos.
        // Os resultados variam conforme a JVM e o Garbage Collector.
        // Esta medição é didática; para rigor, o ideal é usar JMH.
    }
}