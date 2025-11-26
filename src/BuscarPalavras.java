import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class BuscarPalavras {
        
    public static void SerialCPU(String conteudo,String palavraChave){
        
        final String[] palavras = formatarTexto(conteudo);
        int ocorrencias = 0;

        final long tempoInicio = System.nanoTime();

        for(int i = 0;i < palavras.length;i++){
            if (palavras[i].equalsIgnoreCase(palavraChave)){
                ocorrencias++;
            }
        }

        final long tempoFim = System.nanoTime();

        final long tempoExecucao = tempoFim - tempoInicio;

        // System.out.printf("SerialCPU: %d ocorrências em %d ms. Palavra Chave: %s\n",ocorrencias,tempoExecucao,palavraChave);
    }

    public static void ParallelCPU(String conteudo, String palavraChave,int quantidadeThreads){
        try (ExecutorService executorService = Executors.newFixedThreadPool(quantidadeThreads)) {
            
            final long tempoInicio = System.nanoTime();

            Future<Integer>[] resultadosFuturos = new Future[quantidadeThreads];
            final String[] palavras = formatarTexto(conteudo);
            
            int n = palavras.length;
            int tamanhoSublistas = (int) Math.ceil((double) n / quantidadeThreads);

            for (int i = 0; i < resultadosFuturos.length; i++) {
                int inicio = i * tamanhoSublistas;
                int fim = Math.min(inicio + tamanhoSublistas,n);

                if (inicio >= fim) break;

                String[] subStringPalavras = Arrays.copyOfRange(palavras, inicio, fim);
                
                resultadosFuturos[i] = executorService.submit(() -> {
                    return contarOcorrencias(subStringPalavras, palavraChave);
                });
            }

            int resultadoOcorrencias = 0;
            for (int i = 0; i < quantidadeThreads; i++) {
                if (resultadosFuturos[i] != null) {
                    resultadoOcorrencias += resultadosFuturos[i].get();        
                }
            }

            final long tempoFim = System.nanoTime();
            final long tempoExecucao = tempoFim - tempoInicio;

            // System.out.printf("ParallelCPU: %d ocorrências em %d ms. Palavra Chave: %s\n",resultadoOcorrencias,tempoExecucao,palavraChave);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void paralelo(String conteudo, String palavraChave,int quantidadeThreads){
        try (ForkJoinPool forkJoinPool = new ForkJoinPool(quantidadeThreads)) {
            
            String[] texto = formatarTexto(conteudo);
            int n = texto.length;

            int inicio = 0;
            int fim = n - 1;

            final int FATOR_DIVISAO_PARALELA = 4;
            final int LIMITE_MINIMO_SUBLISTAS = Math.max(n / (quantidadeThreads * FATOR_DIVISAO_PARALELA), 10000);

            forkJoinPool.invoke(new ContarOcorrenciasTask(texto, palavraChave, inicio, fim, LIMITE_MINIMO_SUBLISTAS));
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void ParallelGPU(String conteudo) {
        
        



    }

    private static String[] formatarTexto(String conteudo){

        conteudo = conteudo.replaceAll("[\\s\\p{P}\\p{S}&&[^’'-]]+", " ").strip();
        return conteudo.split(" ");
    }

    private static int contarOcorrencias(String[] texto,String palavraChave){
        
        int ocorrencias = 0;

        for(int i = 0;i < texto.length;i++){
            if (texto[i].equalsIgnoreCase(palavraChave)){
                ocorrencias++;
            }
        }

        return ocorrencias;
    }

    private static class ContarOcorrenciasTask extends RecursiveTask<Integer> {
        private static String[] conteudo;
        private static String palavraChave;
        private static int inicio;
        private static int fim;
        private static int LIMITE_MINIMO_SUBLISTAS;

        public ContarOcorrenciasTask(String[] conteudo, String palavraChave, int inicio, int fim, int lIMITE_MINIMO_SUBLISTAS) {
            this.conteudo = conteudo;
            this.palavraChave = palavraChave;
            this.inicio = inicio;
            this.fim = fim;
            LIMITE_MINIMO_SUBLISTAS = lIMITE_MINIMO_SUBLISTAS;
        }

        private static Integer contarOcorrencias(String[] conteudo,int inicio,int fim){
            Integer ocorrencias = 0;

            for (int i = inicio; i <= fim; i++) {
                if (conteudo[i].equalsIgnoreCase(palavraChave)) {
                    ocorrencias++;
                }
            }

            return ocorrencias;
        }

        @Override
        protected Integer compute() {
            if (fim - inicio + 1 <= LIMITE_MINIMO_SUBLISTAS){
                return contarOcorrencias(conteudo, inicio, fim);
            }


            int meio = (inicio + fim) / 2;

            ContarOcorrenciasTask esquerda = new ContarOcorrenciasTask(conteudo, palavraChave, inicio, meio, LIMITE_MINIMO_SUBLISTAS);
            ContarOcorrenciasTask direita = new ContarOcorrenciasTask(conteudo, palavraChave, meio + 1, fim, LIMITE_MINIMO_SUBLISTAS);

            esquerda.fork();
            int resultadodireita = direita.compute();
            int resultadoesquerda = esquerda.join();

            return resultadoesquerda + resultadodireita;


        }

        
    }
}