import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BuscarPalavras {
    
    private static List<String> listaPalavrasChaves;
    
    public static void SerialCPU(String conteudo,String caminho){
        
        final StringBuilder resultado = new StringBuilder();

        final String[] palavras = formatarTexto(conteudo);
        criarPalavrasChaves(palavras);
        
        for (int i = 0; i < listaPalavrasChaves.size(); i++) {
            int contador = 0;
            String palavraChaveAtual = listaPalavrasChaves.get(i);

            for (int j = 0; j < palavras.length; j++) {
                
                if (palavraChaveAtual.equalsIgnoreCase((palavras[j]))) {
                    contador++;
                }
            }
            
            resultado.append(
                String.format("%s - %d \n", palavraChaveAtual,contador)
            );
        }

        Arquivo.criarArquivoResultado(resultado, caminho);
    }

    public static void SerialCPU(String conteudo){
        
        final StringBuilder resultado = new StringBuilder();

        final String[] palavras = formatarTexto(conteudo);
        criarPalavrasChaves(palavras);
        
        for (int i = 0; i < listaPalavrasChaves.size(); i++) {
            int contador = 0;
            String palavraChaveAtual = listaPalavrasChaves.get(i);

            for (int j = 0; j < palavras.length; j++) {
                
                if (palavraChaveAtual.equalsIgnoreCase((palavras[j]))) {
                    contador++;

                    // if (palavraChaveAtual.hashCode() == palavras[j].hashCode() && !palavraChaveAtual.equalsIgnoreCase((palavras[j]))) {
                    //     System.out.printf("%s - %s\n",palavraChaveAtual,palavras[j]);
                    //     System.out.println("fudeu");
                    //     break;
                    // }
                }
            }
            
            resultado.append(
                String.format("%s - %d \n", palavraChaveAtual,contador)
            );
        }

    }

    public static void ParallelCPU(String conteudo, int quantidadeThreads){
        try (ExecutorService executorService = Executors.newFixedThreadPool(quantidadeThreads)) {
            
            Future<String[]>[] resultadosFuturos = new Future[quantidadeThreads];
            String[] palavras = formatarTexto(conteudo);
            
            int n = palavras.length;
            int tamanhoSublistas = (int) Math.ceil((double) n / quantidadeThreads);

            for (int i = 0; i < resultadosFuturos.length; i++) {
                int inicio = i * tamanhoSublistas;
                int fim = Math.min(inicio + tamanhoSublistas,n);

                if (inicio >= fim) break;

                String[] subStringPalavras = Arrays.copyOfRange(palavras, inicio, fim);
                
                resultadosFuturos[i] = executorService.submit(() -> {
                    SerialCPU(String.join(",",subStringPalavras).replace(",", " "));
                    return subStringPalavras;
                });
            }

            for (int i = 0; i < quantidadeThreads; i++) {
                if (resultadosFuturos[i] != null) {
                    resultadosFuturos[i].get();        
                }
            }

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

    private static void criarPalavrasChaves(String[] palavras){
        
        final Set<String> palavrasChaves = new HashSet<>();

        for (int i = 0; i < palavras.length; i++) {
            palavrasChaves.add(palavras[i]);
        }

        listaPalavrasChaves = new ArrayList<>(palavrasChaves);
    }

}