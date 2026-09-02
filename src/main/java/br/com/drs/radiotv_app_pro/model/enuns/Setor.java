package br.com.drs.radiotv_app_pro.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Setor {

    PRESIDENCIA ("PR", "Presidência"),
    DIRECAO_GERAL("DG","Direção Geral"),
    DIRETORIA_EXECUTIVA ("DE", "Administrativa"),
    JURIDICO ("JR", "Departamento Jurídico"),
    JORNALISMO ("JO", "Direção de Jornalismo"),
    REDACAO ("RD", "Redator"),
    PRODUCAO_JORNALISTICA ("PJ", "Produção de Conteúdo"),
    REPORTAGEM_EXTERNA ("RE","Equipes de Rua"),
    APRESENTACAO ("AP","Apresentadores"),
    ESPORTES ("ES", "Esportes"),
    TEMPO_TRANSITO ("TT", "Boletins de Meteorologia e Trânsito"),
    PROGRAMACAO ("PG", "Direção de Programação"),
    PRODUCAO_ARTISTICA ("PA", "Produção de Auditório"),
    DRAMATURGIA ("DR", "Teledramaturgia e Séries — mais comum em TV"),
    LOCUCAO ("LC", "Locutores"),
    DJ_OPERACAO_SOM ("DJ", "Seleção Musical e Programação de Rádio"),
    ENGENHARIA_BROADCAST ("EB", "Engenharia de Transmissão"),
    OPERACAO_MASTER ("OM","Central de Controle Master"),
    ESTUDIOS_CAMERAS ("EC", "Operação de Câmeras"),
    EXTERNA_UP_LINK ("EL","Unidades Móveis"),
    TI_TECNOLOGIA ("TI","Tecnologia da Informação"),
    MANUTENCAO ("MN", "Manutenção Preventiva e Corretiva de Equipamentos"),
    COMERCIAL_VENDAS ("CO", "Venda de Espaço Publicitário"),
    MARKETING ("MA","Marketing"),
    PRODUCAO_COMERCIAL ("PC", "Criação e Produção de Spots"),
    DIGITAL_MULTIMIDIA ("DM", "Redes Sociais"),
    EVENTOS ("EV", "Produção de Eventos"),
    FINANCEIRO ("FI", "Contas a Pagar/Receber"),
    RECURSOS_HUMANOS ("RH", "RH, DP, Folha de Pagamento"),
    COMPRAS_SUPRIMENTOS ("CS", "Aquisição de Materiais"),
    FACILITIES ("FC", "Limpeza, Segurança, Recepção e Manutenção Predial"),
    CLIENTES("CL", "Clientes"),
    AGENCIAS("AG", "Agências"),
    OUTROS("OU", "Outros");

    private String setor ;
    private String descricao;
}
