package br.com.drs.radiotv_app_pro.model.enuns;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Setor {

    PRESIDENCIA ("Presidência"),
    DIRECAO_GERAL("Direção Geral"),
    DIRETORIA_EXECUTIVA ("Administrativa"),
    JURIDICO ("Departamento Jurídico"),
    JORNALISMO ("Direção de Jornalismo"),
    REDACAO ("Redator"),
    PRODUCAO_JORNALISTICA ("Produção de Conteúdo"),
    REPORTAGEM_EXTERNA ("Equipes de Rua"),
    APRESENTACAO ("Apresentadores"),
    ESPORTES ("Esportes"),
    TEMPO_TRANSITO ("Boletins de Meteorologia e Trânsito"),
    PROGRAMACAO ("Direção de Programação"),
    PRODUCAO_ARTISTICA ("Produção de Auditório"),
    DRAMATURGIA ("Teledramaturgia e Séries — mais comum em TV"),
    LOCUCAO ("Locutores"),
    DJ_OPERACAO_SOM ("Seleção Musical e Programação de Rádio"),
    ENGENHARIA_BROADCAST ("Engenharia de Transmissão"),
    OPERACAO_MASTER ("Central de Controle Master"),
    ESTUDIOS_CAMERAS ("Operação de Câmeras"),
    EXTERNA_UP_LINK ("Unidades Móveis"),
    TI_TECNOLOGIA ("Tecnologia da Informação"),
    MANUTENCAO ("Manutenção Preventiva e Corretiva de Equipamentos"),
    COMERCIAL_VENDAS ("Venda de Espaço Publicitário"),
    MARKETING ("Marketing"),
    PRODUCAO_COMERCIAL ("Criação e Produção de Spots"),
    DIGITAL_MULTIMIDIA ("Redes Sociais"),
    EVENTOS ("Produção de Eventos"),
    FINANCEIRO ("Contas a Pagar/Receber"),
    RECURSOS_HUMANOS ("RH, DP, Folha de Pagamento"),
    COMPRAS_SUPRIMENTOS ("Aquisição de Materiais"),
    FACILITIES ("Limpeza, Segurança, Recepção e Manutenção Predial"),
    CLIENTES("Clientes"),
    AGENCIAS("Agências"),
    OUTROS("Outros");

    private String descricao;
}
