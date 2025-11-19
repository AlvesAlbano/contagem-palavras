import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BuscaPalavras {
    
    public static List<String> listaPalavrasChaves;
    
    public static void SerialCPU(String conteudo,String caminho){
        
        final StringBuilder resultado = new StringBuilder();

        final String[] palavras = conteudo.split("[^\\p{L}]+");
        criarPalavrasChaves(palavras);
        
        for (int i = 0; i < listaPalavrasChaves.size(); i++) {
            int contador = 1;
            String palavraChaveAtual = listaPalavrasChaves.get(i);
            for (int j = 0; j < palavras.length; j++) {
                
                if (palavraChaveAtual.equals(palavras[j])){
                    contador++;
                }
            }
            
            resultado.append(
                String.format("%s - %d \n", palavraChaveAtual,contador)
            );
        }

        Arquivo.criarArquivoResultado(resultado, caminho);
    }

    private static void criarPalavrasChaves(String[] palavras){
        
        final Set<String> palavrasChaves = new HashSet<>();

        for (int i = 0; i < palavras.length; i++) {
            palavrasChaves.add(palavras[i]);
        }
        listaPalavrasChaves = new ArrayList<>(palavrasChaves);
    }

    public static void ParallelCPU(String conteudo){

    }

    public static void ParallelGPU(String conteudo){

    }

    public static void gerarPalavrasChaves(){

    }
}
