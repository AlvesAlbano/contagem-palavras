import java.io.File;

public class App {
    public static void main(String[] args) throws Exception {

        final String[] arquivos = new String[]{
            "Amostras/MobyDick-217452.txt", // tamanho: 1.215.909
            "Amostras/Dracula-165307.txt", // tamanho: 849.321
            "Amostras/DonQuixote-388208.txt", // tamanho: 2.091.965
        };

        final int iteracoes = 1_500;
        final int quantidadeThreads = 4;
        final String palavraChave = "he".toLowerCase();

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
}