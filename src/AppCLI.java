import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AppCLI {
    private static final String[] arquivos = new String[]{
            "Amostras/MobyDick-217452.txt", // tamanho: 1.215.909
            "Amostras/Dracula-165307.txt", // tamanho: 849.321
            "Amostras/DonQuixote-388208.txt", // tamanho: 2.091.965
        };
    
    public static void main(String[] args) {
        
        try (Scanner input = new Scanner(System.in);) {
            
            while(true) {
                
                System.out.println("Digite a quantidade de iterações: ");
                final int iteracoes = input.nextInt();
                
                System.out.println("Digite a quantidade de threads a serem usadas: ");
                final int quantidadeThreads = input.nextInt();

            
                System.out.println("Digite a palavra para ser pesquisada nos livros: ");
                
                final String palavraChave = input.next().toLowerCase();
                
                desempenhoAlgoritmos(palavraChave, iteracoes, quantidadeThreads);
                gerarGraficos("python", "src/graficos.py");

                System.out.println("Continuar?");
                System.out.println("1 - Sim");
                System.out.println("2 - Não");
                
                final int opcao = input.nextInt();
                
                if (opcao != 1) {
                    break;
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void desempenhoAlgoritmos(String palavraChave, int iteracoes, int quantidadeThreads){
        for (int i = 0; i < arquivos.length; i++) {
            String arquivoCaminho = arquivos[i];
            String conteudo = Arquivo.lerArquivo(arquivoCaminho);
            String[] textoFormatado = FormatarTexto.formatar(conteudo);
            String nomeLivro = new File(arquivoCaminho).getName().replace(".txt", "");
            System.out.println(nomeLivro);
            Desempenho.gerarRelatorioSerial(() -> BuscarPalavras.SerialCPU(textoFormatado,palavraChave), iteracoes, nomeLivro);
        }
    
        for (int i = 0; i < arquivos.length; i++) {
            String arquivoCaminho = arquivos[i];
            String conteudo = Arquivo.lerArquivo(arquivoCaminho);
            String nomeLivro = new File(arquivoCaminho).getName().replace(".txt", "");
            String[] textoFormatado = FormatarTexto.formatar(conteudo);
            System.out.println(nomeLivro);
            Desempenho.gerarRelatorioParalelo(() -> BuscarPalavras.ParallelCPU(textoFormatado, palavraChave, quantidadeThreads), iteracoes, nomeLivro,quantidadeThreads);
        }
    
        for (int i = 0; i < arquivos.length; i++) {
            String arquivoCaminho = arquivos[i];
            String conteudo = Arquivo.lerArquivo(arquivoCaminho);
            String nomeLivro = new File(arquivoCaminho).getName().replace(".txt", "");
            String[] textoFormatado = FormatarTexto.formatar(conteudo);
            System.out.println(nomeLivro);
            Desempenho.gerarRelatorioParaleloGPU(() -> BuscarPalavras.ParallelGPU(textoFormatado, palavraChave), iteracoes, nomeLivro);
        }
    }

    private static void gerarGraficos(String pythonExecutable, String scriptPath) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add(pythonExecutable); // ex: "python" ou "python3" ou caminho completo
        cmd.add(scriptPath);       // ex: "/caminho/para/graficos.py"

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true); // combina stderr com stdout
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        System.out.println("Exit code: " + exitCode);
    }
}
