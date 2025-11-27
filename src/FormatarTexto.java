public class FormatarTexto {
    
    public static String[] formatar(String conteudo){
        conteudo = conteudo.replaceAll("[\\s\\p{P}\\p{S}&&[^’'-]]+", " ").strip().toLowerCase();
        return conteudo.split(" ");
    }
}
