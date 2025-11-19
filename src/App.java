public class App {
    public static void main(String[] args) throws Exception {

        final String[] arquivos = new String[]{
            "Amostras/MobyDick-217452.txt",
            "Amostras/Dracula-165307.txt",
            "Amostras/DonQuixote-388208.txt",
        };


        // System.out.println("Hello, World!");

        // for (String string : arquivos) {
        //     System.out.println(Arquivo.lerArquivo(string));
        // }
        
        
        for (int i = 0; i < arquivos.length; i++) {
            String arquivo = arquivos[i];
            String conteudo = Arquivo.lerArquivo(arquivo);
            BuscaPalavras.SerialCPU(conteudo,arquivo);
        }
    }
}