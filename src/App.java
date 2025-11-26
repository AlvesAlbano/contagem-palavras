import java.io.File;

public class App {
    public static void main(String[] args) throws Exception {

        final String[] arquivos = new String[]{
            "Amostras/MobyDick-217452.txt", // tamanho: 1.215.909
            "Amostras/Dracula-165307.txt", // tamanho: 849.321
            "Amostras/DonQuixote-388208.txt", // tamanho: 2.091.965
        };

        final int iteracoes = 10;
        final int quantidadeThreads = 4;

        for (int i = 0; i < arquivos.length; i++) {
            String arquivoCaminho = arquivos[i];
            String conteudo = Arquivo.lerArquivo(arquivoCaminho);
            String nomeLivro = new File(arquivoCaminho).getName().replace(".txt", "");
            Desempenho.gerarRelatorioSerial(() -> BuscarPalavras.SerialCPU(conteudo), iteracoes, nomeLivro);
        }

        for (int i = 0; i < arquivos.length; i++) {
            String arquivoCaminho = arquivos[i];
            String conteudo = Arquivo.lerArquivo(arquivoCaminho);
            String nomeLivro = new File(arquivoCaminho).getName().replace(".txt", "");
            Desempenho.gerarRelatorioParalelo(() -> BuscarPalavras.ParallelCPU(conteudo, quantidadeThreads), iteracoes, nomeLivro,quantidadeThreads);
        }
    }
}