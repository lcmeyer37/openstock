/*
 * Copyright (C) 2019 lucasmeyer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package panels.analisadorasset.offlinetrader;

import static frames.analisadorasset.grafico.adicionarindicador.submodulopai;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author lucasmeyer
 */
public class submoduloofflinetrader extends javax.swing.JPanel
{
     /*
    submodulo offline trader, utilizado para manual trading tests e algotrading tests
    */
    
    // <editor-fold defaultstate="collapsed" desc="Variáveis Públicas">
    //analisador de asset pai, contem modulos para analises do asset (ateh o momento grafico e trader)
    public panels.analisadorasset.analisadorasset aassetpai; 
    //offline trader utilizado por este submodulo
    public mierclasses.mcofflinetrader otrader; 
    //classe interpretadora de bearcode (contem o codigo relacionado ao bot trader em uso)
    public mierclasses.mcbctradingbotinterpreter mcbctradingbot;
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="CONSTRUTOR">
    public submoduloofflinetrader(panels.analisadorasset.analisadorasset aapai)
    {
        initComponents();
        
        aassetpai = aapai;
        otrader = new mierclasses.mcofflinetrader(this);
        
        //jPanelTraderbot.setVisible(false);
        inicializarsubmoduloofflinetrader();
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Inicialização e Recarregamento">
    void inicializarsubmoduloofflinetrader()
    {
        //set jPanel de transacoes
        jPanelLinhasTransacoes.setLayout(new java.awt.GridLayout(500,1));
        
        //associar o simbolo utilizado pelo trader
        otrader.recriarofflinetrader(aassetpai.assetsimbolo);
        
        //o usuario pode alterar os parametros do trader bot quando desativado
        jTextFieldParametrosTraderbot.setEditable(false);
            
        //popular o trader bot pela primeira vez com primeiro script disponivel
        popularcomboboxtraderbotsdisponiveis();
        String tbotselecionadostring = (String)jComboBoxScriptAtualTraderbot.getSelectedItem();
        String idbc = (tbotselecionadostring.split("\\[")[1]).split("\\]")[0];
        String parametrosbc = jTextFieldParametrosTraderbot.getText();
        repopularbearcodetraderbot(idbc,parametrosbc);
        
        //atualizar informacoes de bid ask atual
        otrader.atualizarbidask();
        //atualizar informacoes das janelas de buy/sell/funds
        jLabelComprar.setText("Buy " + otrader.simbolo.toUpperCase());
        jLabelVender.setText("Sell " + otrader.simbolo.toUpperCase());
        jTextFieldMelhorAsk.setText(String.format( "%.6f",otrader.melhorask));
        jTextFieldMelhorBid.setText(String.format( "%.6f",otrader.melhorbid));
        jTextFieldMoedaBaseAtual.setText(String.format( "%.6f",otrader.quantidademoedabase));
        jTextFieldMoedaCotacaoAtual.setText(String.format( "%.6f",otrader.quantidademoedacotacao));
        jLabelTotalFundos.setText("Total (Quote Value): " + String.format( "%.6f",otrader.totalfundos_moedacotacao()));
        jLabelFeeCompra.setText("Fee " + String.format( "%.2f",100*otrader.feecompra) + "%");
        jLabelFeeVenda.setText("Fee " + String.format( "%.2f",100*otrader.feevenda) + "%");
        
        //rodar o trader (caso ativado)
        rodartraderbot();

        //atualizar lista de transacoes offline
        jPanelLinhasTransacoes.removeAll();
        for (int i = 0; i < otrader.transacoes.size(); i++)
        {
            jPanelLinhasTransacoes.add(new panels.analisadorasset.offlinetrader.itemtransacao(otrader.transacoes.get(i)));
        }
        
        this.validate();
        this.repaint();
    }
    
    public void recarregardadossubmoduloofflinetrader()
    {
        //caso o simbolo tenha sido alterado, as informacoes do trader precisam ser resetadas
        if (aassetpai.assetsimbolo.equals(otrader.simbolo) == false)
            otrader.recriarofflinetrader(aassetpai.assetsimbolo);
        
        //atualizar informacoes de bid ask atual
        otrader.atualizarbidask();
        //atualizar informacoes das janelas de buy/sell/funds
        jLabelComprar.setText("Buy " + otrader.simbolo.toUpperCase());
        jLabelVender.setText("Sell " + otrader.simbolo.toUpperCase());
        jTextFieldMelhorAsk.setText(String.format( "%.6f",otrader.melhorask));
        jTextFieldMelhorBid.setText(String.format( "%.6f",otrader.melhorbid));
        jTextFieldMoedaBaseAtual.setText(String.format( "%.6f",otrader.quantidademoedabase));
        jTextFieldMoedaCotacaoAtual.setText(String.format( "%.6f",otrader.quantidademoedacotacao));
        jLabelTotalFundos.setText("Total (Quote Value): " + String.format( "%.6f",otrader.totalfundos_moedacotacao()));
        jLabelFeeCompra.setText("Fee " + String.format( "%.2f",100*otrader.feecompra) + "%");
        jLabelFeeVenda.setText("Fee " + String.format( "%.2f",100*otrader.feevenda) + "%");
        
        //rodar o trader (caso ativado)
        rodartraderbot();

        //atualizar lista de transacoes offline
        jPanelLinhasTransacoes.removeAll();
        for (int i = 0; i < otrader.transacoes.size(); i++)
        {
            jPanelLinhasTransacoes.add(new panels.analisadorasset.offlinetrader.itemtransacao(otrader.transacoes.get(i)));
        }
        
        this.validate();
        this.repaint();
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Implementação para Realização de Transações Manuais">
    public String realizardepositoousaque(String tipomoeda, String depositoousaque, String valortransacao)
    {
        try
        {
            String resposta = "erro - desconhecido";
            
            if (tipomoeda.equals("base") == true)
            {
                if (depositoousaque.equals("deposito") == true)
                {
                    double valor = Double.valueOf(valortransacao);
                    resposta = otrader.realizardeposito_base(valor);
                }
                else if (depositoousaque.equals("saque") == true)
                {
                    double valor = Double.valueOf(valortransacao);
                    resposta = otrader.realizarsaque_base(valor);
                }
            }
            else if (tipomoeda.equals("cotacao") == true)
            {
                if (depositoousaque.equals("deposito") == true)
                {
                    double valor = Double.valueOf(valortransacao);
                    resposta = otrader.realizardeposito_cotacao(valor);
                }
                else if (depositoousaque.equals("saque") == true)
                {
                    double valor = Double.valueOf(valortransacao);
                    resposta = otrader.realizarsaque_cotacao(valor);
                }
            }
            
            if (resposta.equals("ok") == true)
            {
                //considerando que a transacao foi bem sucedida, recarregar submodulo para mostrar nova linha de transacao
                recarregardadossubmoduloofflinetrader();
            }
            
            return resposta;
        }
        catch (Exception ex)
        {
            //caso aja algum problema, ignorar todo o processo de transacao
            return "erro - " + ex.getMessage();
        }
    }
    
    void atualizarvalortotalprevisaocompra()
    {
        try
        {
            double quantidadecompra = Double.valueOf(jTextFieldComprarQuantidade.getText());
            double custototal = otrader.custototalcompra_basecotacao(quantidadecompra);
            jTextFieldComprarTotal.setText(String.format( "%.6f",custototal));
        }
        catch (Exception ex)
        {
            jTextFieldComprarTotal.setText("");
        }
    }
    
    public void realizarcompra(double qbasecomprar, boolean mostrarmensagem)
    {
        try
        {
            String resposta = "erro - desconhecido";
            double valor = Double.valueOf(qbasecomprar);
            resposta = otrader.realizarcompra_basecotacao(valor);

            if (resposta.equals("ok") == true)
            {
                //considerando que a transacao foi bem sucedida, recarregar submodulo para mostrar nova linha de transacao
                recarregardadossubmoduloofflinetrader();
            }
            else
            {
                mierclasses.mcfuncoeshelper.mostrarmensagem(resposta);
            }
        }
        catch (Exception ex)
        {
            //caso aja algum problema, ignorar todo o processo de transacao
            if (mostrarmensagem == true)
                mierclasses.mcfuncoeshelper.mostrarmensagem("erro - " + ex.getMessage());
        }
    }
    
    void atualizarvalortotalprevisaovenda()
    {
        try
        {
            double quantidadevenda = Double.valueOf(jTextFieldVenderQuantidade.getText());
            double ganhototal = otrader.ganhototalvenda_basecotacao(quantidadevenda);
            jTextFieldVenderTotal.setText(String.format( "%.6f",ganhototal));
        }
        catch (Exception ex)
        {
            jTextFieldVenderTotal.setText("");
        }
    }
    
    public void realizarvenda(double qbasevender, boolean mostrarmensagem)
    {
        try
        {
            String resposta = "erro - desconhecido";
            double valor = Double.valueOf(jTextFieldVenderQuantidade.getText());
            resposta = otrader.realizarvenda_basecotacao(valor);

            if (resposta.equals("ok") == true)
            {
                //considerando que a transacao foi bem sucedida, recarregar submodulo para mostrar nova linha de transacao
                recarregardadossubmoduloofflinetrader();
            }
            else
            {
                mierclasses.mcfuncoeshelper.mostrarmensagem(resposta);
            }
        }
        catch (Exception ex)
        {
            //caso aja algum problema, ignorar todo o processo de transacao
            if (mostrarmensagem == true)
                mierclasses.mcfuncoeshelper.mostrarmensagem("erro - " + ex.getMessage());

        }
    }
    
    public void realizarcompratudo(boolean mostrarmensagem)
    {
        try
        {
            String resposta = "erro - desconhecido";
            resposta = otrader.realizarcompratudo_basecotacao();

            if (resposta.equals("ok") == true)
            {
                //considerando que a transacao foi bem sucedida, recarregar submodulo para mostrar nova linha de transacao
                recarregardadossubmoduloofflinetrader();
            }
            else
            {
                mierclasses.mcfuncoeshelper.mostrarmensagem(resposta);
            }
        }
        catch (Exception ex)
        {
            //caso aja algum problema, ignorar todo o processo de transacao
            if (mostrarmensagem == true)
                mierclasses.mcfuncoeshelper.mostrarmensagem("erro - " + ex.getMessage());

        }
    }
    
    public void realizarvendatudo(boolean mostrarmensagem)
    {
        try
        {
            String resposta = "erro - desconhecido";
            resposta = otrader.realizarvendatudo_basecotacao();

            if (resposta.equals("ok") == true)
            {
                //considerando que a transacao foi bem sucedida, recarregar submodulo para mostrar nova linha de transacao
                recarregardadossubmoduloofflinetrader();
            }
            else
            {
                mierclasses.mcfuncoeshelper.mostrarmensagem(resposta);
            }
        }
        catch (Exception ex)
        {
            //caso aja algum problema, ignorar todo o processo de transacao
            if (mostrarmensagem == true)
                mierclasses.mcfuncoeshelper.mostrarmensagem("erro - " + ex.getMessage());

        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Implementação para Realização de Transações com Trader Bot">
    void popularcomboboxtraderbotsdisponiveis()
    {
        //abrir documento /arquivosconfig/indicadores.mfxconfig para ler indicadores disponiveis para uso
        //e criar itens com indicadores disponiveis no jComboBoxIndicadoresDisponiveis
        
        try
        {
            jComboBoxScriptAtualTraderbot.removeAllItems();
            
            String rootjar = mierclasses.mcfuncoeshelper.retornarpathbaseprograma();
            String cindicconfig = rootjar + "/outfiles/bearcode/traderbots/traderbots.mfxconfig";
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cindicconfig);
            
            File xmlArquivo = new File(cindicconfig);
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        
            DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
        
            Document document = dbuilder.parse(xmlArquivo);
            
            NodeList listaIndicadoresDisponiveis = document.getElementsByTagName("Traderbot");
            
            for (int i = 0; i < listaIndicadoresDisponiveis.getLength(); i++)
            {
                Node nodeIndicador = listaIndicadoresDisponiveis.item(i);
                
                if (nodeIndicador.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element elIndicador = (Element) nodeIndicador;
                    
                    String id =  elIndicador.getElementsByTagName("BCID").item(0).getTextContent();
                    String nome = elIndicador.getElementsByTagName("Name").item(0).getTextContent();
                    String caminhobcode = elIndicador.getElementsByTagName("BearcodeFile").item(0).getTextContent();
                    
                    jComboBoxScriptAtualTraderbot.addItem(nome + " [" + id + "]");
                }
            }
        }
        catch (Exception e) 
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("Uma exceção ocorreu: " + e.toString());
        }
    }
    
    void repopularbearcodetraderbot(String idbearcode, String parametrosbearcode)
    {
        //funcao para popular mcbctradingbotinterpreter beacode trader bot engine utilizado por este submodulo
        
        //public mcbcindicatorinterpreter(String tipo, String id, String nome, String codbcjs, String paramsbcjs)
        //funcao para criar um novo mcbcindicatorinterpreter, que sera utilizado para
        //interpretar o codigo bearcode e gerar os valores x e y relacionados a este indicador
        
        //String tipobci = "indicador";
        //mierclasses.mcfuncoeshelper.mostrarmensagem("tipobci: " + tipobci);
        String idbci = idbearcode;
        //mierclasses.mcfuncoeshelper.mostrarmensagem("idbci: " + idbci);
        String nomebci = "";
        String caminhoarquivobci = "";
        // <editor-fold defaultstate="collapsed" desc="encontrar caminho e nome deste bearcode utilizando o id">
    
        try
        {
            String rootjar = mierclasses.mcfuncoeshelper.retornarpathbaseprograma();
            String cindicconfig = rootjar + "/outfiles/bearcode/traderbots/traderbots.mfxconfig";
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cindicconfig);
            
            File xmlArquivo = new File(cindicconfig);
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        
            DocumentBuilder dbuilder = dbfactory.newDocumentBuilder();
        
            Document document = dbuilder.parse(xmlArquivo);
            
            NodeList listaIndicadoresDisponiveis = document.getElementsByTagName("Traderbot");
            
            for (int i = 0; i < listaIndicadoresDisponiveis.getLength(); i++)
            {
                Node nodeIndicador = listaIndicadoresDisponiveis.item(i);

                if (nodeIndicador.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element elIndicador = (Element) nodeIndicador;
                    
                    String id =  elIndicador.getElementsByTagName("BCID").item(0).getTextContent();
                    //mierclasses.mcfuncoeshelper.mostrarmensagem("id: " + id);
                    
                    if (id.equals(idbci) == true)
                    {
                        String nome = elIndicador.getElementsByTagName("Name").item(0).getTextContent();
                        nomebci = nome;
                        //mierclasses.mcfuncoeshelper.mostrarmensagem("nome: " + nome);
                        String arquivobcode = elIndicador.getElementsByTagName("BearcodeFile").item(0).getTextContent();
                        //mierclasses.mcfuncoeshelper.mostrarmensagem("arquivobcode: " + arquivobcode);
                        caminhoarquivobci = rootjar + "/outfiles/bearcode/traderbots/" + arquivobcode;
                        //mierclasses.mcfuncoeshelper.mostrarmensagem("caminhoarquivobci: " + caminhoarquivobci);
                        break;
                    }
                    
                }
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            mierclasses.mcfuncoeshelper.mostrarmensagem("Uma exceção ocorreu: " + e.getLocalizedMessage());
        }
        //</editor-fold>
        
        String conteudoscriptbci = "";
        //passar para conteudoscriptbci o codigo javascript bearcode
        java.util.List<String> lines = java.util.Collections.emptyList(); 
        try
        { 
            lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(caminhoarquivobci), java.nio.charset.StandardCharsets.UTF_8); 
        
            for (int i = 0; i < lines.size(); i++)
            {
                conteudoscriptbci = conteudoscriptbci + "\n" + lines.get(i);
                
            }
            //mierclasses.mcfuncoeshelper.mostrarmensagem("conteudoscriptbci: " + conteudoscriptbci);
        } 
        catch (Exception e) 
        { 
            e.printStackTrace();
            mierclasses.mcfuncoeshelper.mostrarmensagem("Uma exceção ocorreu: " + e.toString());
        } 
        
        mcbctradingbot = new mierclasses.mcbctradingbotinterpreter(idbci, nomebci, conteudoscriptbci, parametrosbearcode);
    }
    
    //variavel que diz se o traderbot esta rodando ou nao
    boolean botativado = false;
    void ativardesativartraderbot()
    {
        botativado = !botativado;
        
        if (botativado == false)
        {
            //o usuario pode alterar os parametros do trader bot quando desativado
            jTextFieldParametrosTraderbot.setEditable(true);
            
            jLabelStatusTrader.setText("Status: (not running)");
            jLabelTraderbot.setText("Trader Bot Controller");
            jButtonAtivarDesativarTrader.setText("Activate");
        }
        else if (botativado == true)
        {
            //o usuario pode alterar os parametros do trader bot quando desativado
            jTextFieldParametrosTraderbot.setEditable(false);
            
            //repopular o trader bot
            popularcomboboxtraderbotsdisponiveis();
            String tbotselecionadostring = (String)jComboBoxScriptAtualTraderbot.getSelectedItem();
            String idbc = (tbotselecionadostring.split("\\[")[1]).split("\\]")[0];
            String parametrosbc = jTextFieldParametrosTraderbot.getText();
            repopularbearcodetraderbot(idbc,parametrosbc);
        
            jLabelStatusTrader.setText("Status: (running)");
            jButtonAtivarDesativarTrader.setText("Deactivate");
        }
            
    }
    
    java.util.List<mierclasses.mccandle> candlesrunanterior;
    void rodartraderbot()
    {
        //o trader bot soh roda quando esta ativado E quando percebe
        //que a lista de candles foi atualizada
        
        if (botativado == true)
        {
            java.util.List<mierclasses.mccandle> candlesusar = aassetpai.subgrafico.mcg.candlesatual;
            
            if (candlesusar != candlesrunanterior)
            {
                candlesrunanterior = candlesusar;
                
                mcbctradingbot.rodarscript
                    (
                        candlesrunanterior,
                        otrader.quantidademoedabase, 
                        otrader.quantidademoedacotacao, 
                        otrader.melhorbid,
                        otrader.melhorask,
                        otrader.feecompra,
                        otrader.feevenda,
                        false,
                        null
                    );

                String traderbot_move = (String)mcbctradingbot.respostatradermove_lastrun; 
                double traderbot_supportamount = ((double[]) mcbctradingbot.respostaquantidadesuporte_lastrun)[0];

                if (traderbot_move.equals("hold"))
                {
                    //significa que nada deve ser feito
                    jLabelTraderbot.setText("Trader Bot Controller (last decision: HOLD)");
                }
                else if (traderbot_move.equals("buyall"))
                {
                    //quer dizer que o bot deve comprar o maximo de moeda base que puder
                    realizarcompratudo(false);
                    jLabelTraderbot.setText("Trader Bot Controller (last decision: BUY ALL)");
                }
                else if (traderbot_move.equals("sellall"))
                {
                    //quer dizer que o bot deve vender o maximo de moeda base que puder
                    realizarvendatudo(false);
                    jLabelTraderbot.setText("Trader Bot Controller (last decision: SELL ALL)");
                }
                else if (traderbot_move.equals("buyamount"))
                {
                    //significa que o bot deve comprar uma quantidade especifica de moeda base
                    realizarcompra(traderbot_supportamount,false);   
                    jLabelTraderbot.setText("Trader Bot Controller (last decision: BUY AMOUNT)");
                }
                else if (traderbot_move.equals("sellamount"))
                {
                    //significa que o bot deve comprar uma quantidade especifica de moeda base
                    realizarvenda(traderbot_supportamount,false); 
                    jLabelTraderbot.setText("Trader Bot Controller (last decision: SELL AMOUNT)");
                }
            }
            
        }
    }
    //</editor-fold>
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jPanelCompraManual = new javax.swing.JPanel();
        jLabelComprarQuantidade = new javax.swing.JLabel();
        jTextFieldComprarQuantidade = new javax.swing.JTextField();
        jLabelComprarTotal = new javax.swing.JLabel();
        jTextFieldComprarTotal = new javax.swing.JTextField();
        jButtonComprarManual = new javax.swing.JButton();
        jPanelSubComprar = new javax.swing.JPanel();
        jLabelComprar = new javax.swing.JLabel();
        jLabelMelhorAsk = new javax.swing.JLabel();
        jTextFieldMelhorAsk = new javax.swing.JTextField();
        jLabelFeeCompra = new javax.swing.JLabel();
        jButtonComprarManualTudo = new javax.swing.JButton();
        jPanelVendaManual = new javax.swing.JPanel();
        jLabelVenderQuantidade = new javax.swing.JLabel();
        jTextFieldVenderQuantidade = new javax.swing.JTextField();
        jLabelVenderTotal = new javax.swing.JLabel();
        jTextFieldVenderTotal = new javax.swing.JTextField();
        jButtonVenderManual = new javax.swing.JButton();
        jPanelSubVender = new javax.swing.JPanel();
        jLabelVender = new javax.swing.JLabel();
        jLabelMelhorBid = new javax.swing.JLabel();
        jTextFieldMelhorBid = new javax.swing.JTextField();
        jLabelFeeVenda = new javax.swing.JLabel();
        jButtonVenderManualTudo = new javax.swing.JButton();
        jPanelFundos = new javax.swing.JPanel();
        jPanelSubFundos = new javax.swing.JPanel();
        jLabelFundos = new javax.swing.JLabel();
        jLabelMoedaBaseAtual = new javax.swing.JLabel();
        jLabelMoedaCotacaoAtual = new javax.swing.JLabel();
        jTextFieldMoedaBaseAtual = new javax.swing.JTextField();
        jTextFieldMoedaCotacaoAtual = new javax.swing.JTextField();
        jButtonAlterarFundos = new javax.swing.JButton();
        jLabelTotalFundos = new javax.swing.JLabel();
        jPanelTransacoes = new javax.swing.JPanel();
        jPanelHeaderTransacoes = new javax.swing.JPanel();
        jPanelSub = new javax.swing.JPanel();
        jLabelID = new javax.swing.JLabel();
        jLabelTipoTransacao = new javax.swing.JLabel();
        jLabelPrecoTransacao = new javax.swing.JLabel();
        jLabelQuantidadeTransacao = new javax.swing.JLabel();
        jLabelTimestampTransacao = new javax.swing.JLabel();
        jPanelSubTransacoes = new javax.swing.JPanel();
        jLabelTransacoes = new javax.swing.JLabel();
        jScrollPaneLinhasTransacoes = new javax.swing.JScrollPane();
        jPanelLinhasTransacoes = new javax.swing.JPanel();
        jPanelTraderbot = new javax.swing.JPanel();
        jPanelSubTraderbot = new javax.swing.JPanel();
        jLabelTraderbot = new javax.swing.JLabel();
        jLabelScriptAtualTraderbot = new javax.swing.JLabel();
        jComboBoxScriptAtualTraderbot = new javax.swing.JComboBox<>();
        jButtonAtivarDesativarTrader = new javax.swing.JButton();
        jLabelStatusTrader = new javax.swing.JLabel();
        jLabelScriptAtualTraderbot1 = new javax.swing.JLabel();
        jTextFieldParametrosTraderbot = new javax.swing.JTextField();

        setBackground(new java.awt.Color(55, 55, 55));

        jPanelCompraManual.setBackground(new java.awt.Color(120, 120, 120));

        jLabelComprarQuantidade.setForeground(new java.awt.Color(255, 255, 255));
        jLabelComprarQuantidade.setText("Base Amount:");

        jTextFieldComprarQuantidade.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldComprarQuantidade.addCaretListener(new javax.swing.event.CaretListener()
        {
            public void caretUpdate(javax.swing.event.CaretEvent evt)
            {
                jTextFieldComprarQuantidadeCaretUpdate(evt);
            }
        });

        jLabelComprarTotal.setForeground(new java.awt.Color(255, 255, 255));
        jLabelComprarTotal.setText("Quote Cost Estimate:");

        jTextFieldComprarTotal.setEditable(false);
        jTextFieldComprarTotal.setBackground(new java.awt.Color(120, 120, 120));
        jTextFieldComprarTotal.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldComprarTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jButtonComprarManual.setText("Buy Market");
        jButtonComprarManual.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonComprarManualActionPerformed(evt);
            }
        });

        jPanelSubComprar.setBackground(new java.awt.Color(35, 35, 35));

        jLabelComprar.setForeground(new java.awt.Color(255, 255, 255));
        jLabelComprar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelComprar.setText("Buy SYMBOL");

        javax.swing.GroupLayout jPanelSubComprarLayout = new javax.swing.GroupLayout(jPanelSubComprar);
        jPanelSubComprar.setLayout(jPanelSubComprarLayout);
        jPanelSubComprarLayout.setHorizontalGroup(
            jPanelSubComprarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelComprar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelSubComprarLayout.setVerticalGroup(
            jPanelSubComprarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelComprar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
        );

        jLabelMelhorAsk.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMelhorAsk.setText("Best Ask to Buy:");

        jTextFieldMelhorAsk.setEditable(false);
        jTextFieldMelhorAsk.setBackground(new java.awt.Color(120, 120, 120));
        jTextFieldMelhorAsk.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldMelhorAsk.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldMelhorAsk.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jTextFieldMelhorAskActionPerformed(evt);
            }
        });

        jLabelFeeCompra.setFont(new java.awt.Font("Lucida Grande", 2, 12)); // NOI18N
        jLabelFeeCompra.setForeground(new java.awt.Color(255, 255, 255));
        jLabelFeeCompra.setText("Fee");

        jButtonComprarManualTudo.setText("Buy All");
        jButtonComprarManualTudo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonComprarManualTudoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelCompraManualLayout = new javax.swing.GroupLayout(jPanelCompraManual);
        jPanelCompraManual.setLayout(jPanelCompraManualLayout);
        jPanelCompraManualLayout.setHorizontalGroup(
            jPanelCompraManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelSubComprar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelCompraManualLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelCompraManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelCompraManualLayout.createSequentialGroup()
                        .addComponent(jLabelFeeCompra, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonComprarManualTudo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonComprarManual))
                    .addGroup(jPanelCompraManualLayout.createSequentialGroup()
                        .addGroup(jPanelCompraManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelMelhorAsk)
                            .addComponent(jLabelComprarQuantidade)
                            .addComponent(jLabelComprarTotal))
                        .addGap(23, 23, 23)
                        .addGroup(jPanelCompraManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldComprarTotal)
                            .addComponent(jTextFieldMelhorAsk)
                            .addComponent(jTextFieldComprarQuantidade))))
                .addContainerGap())
        );
        jPanelCompraManualLayout.setVerticalGroup(
            jPanelCompraManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelCompraManualLayout.createSequentialGroup()
                .addComponent(jPanelSubComprar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCompraManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMelhorAsk)
                    .addComponent(jTextFieldMelhorAsk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCompraManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelComprarQuantidade)
                    .addComponent(jTextFieldComprarQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelCompraManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelComprarTotal)
                    .addComponent(jTextFieldComprarTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanelCompraManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonComprarManual)
                    .addComponent(jLabelFeeCompra)
                    .addComponent(jButtonComprarManualTudo))
                .addContainerGap())
        );

        jPanelVendaManual.setBackground(new java.awt.Color(120, 120, 120));

        jLabelVenderQuantidade.setForeground(new java.awt.Color(255, 255, 255));
        jLabelVenderQuantidade.setText("Base Amount:");

        jTextFieldVenderQuantidade.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldVenderQuantidade.addCaretListener(new javax.swing.event.CaretListener()
        {
            public void caretUpdate(javax.swing.event.CaretEvent evt)
            {
                jTextFieldVenderQuantidadeCaretUpdate(evt);
            }
        });

        jLabelVenderTotal.setForeground(new java.awt.Color(255, 255, 255));
        jLabelVenderTotal.setText("Quote Gain Estimate:");

        jTextFieldVenderTotal.setEditable(false);
        jTextFieldVenderTotal.setBackground(new java.awt.Color(120, 120, 120));
        jTextFieldVenderTotal.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldVenderTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jButtonVenderManual.setText("Sell Market");
        jButtonVenderManual.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonVenderManualActionPerformed(evt);
            }
        });

        jPanelSubVender.setBackground(new java.awt.Color(35, 35, 35));

        jLabelVender.setForeground(new java.awt.Color(255, 255, 255));
        jLabelVender.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelVender.setText("Sell SYMBOL");

        javax.swing.GroupLayout jPanelSubVenderLayout = new javax.swing.GroupLayout(jPanelSubVender);
        jPanelSubVender.setLayout(jPanelSubVenderLayout);
        jPanelSubVenderLayout.setHorizontalGroup(
            jPanelSubVenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelVender, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelSubVenderLayout.setVerticalGroup(
            jPanelSubVenderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelVender, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
        );

        jLabelMelhorBid.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMelhorBid.setText("Best Bid to Sell:");

        jTextFieldMelhorBid.setEditable(false);
        jTextFieldMelhorBid.setBackground(new java.awt.Color(120, 120, 120));
        jTextFieldMelhorBid.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldMelhorBid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabelFeeVenda.setFont(new java.awt.Font("Lucida Grande", 2, 12)); // NOI18N
        jLabelFeeVenda.setForeground(new java.awt.Color(255, 255, 255));
        jLabelFeeVenda.setText("Fee");

        jButtonVenderManualTudo.setText("Sell All");
        jButtonVenderManualTudo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonVenderManualTudoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelVendaManualLayout = new javax.swing.GroupLayout(jPanelVendaManual);
        jPanelVendaManual.setLayout(jPanelVendaManualLayout);
        jPanelVendaManualLayout.setHorizontalGroup(
            jPanelVendaManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelSubVender, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelVendaManualLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelVendaManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelVendaManualLayout.createSequentialGroup()
                        .addComponent(jLabelFeeVenda, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                        .addComponent(jButtonVenderManualTudo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonVenderManual))
                    .addGroup(jPanelVendaManualLayout.createSequentialGroup()
                        .addGroup(jPanelVendaManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelMelhorBid)
                            .addComponent(jLabelVenderQuantidade)
                            .addComponent(jLabelVenderTotal))
                        .addGap(23, 23, 23)
                        .addGroup(jPanelVendaManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldMelhorBid)
                            .addComponent(jTextFieldVenderQuantidade)
                            .addComponent(jTextFieldVenderTotal, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        jPanelVendaManualLayout.setVerticalGroup(
            jPanelVendaManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelVendaManualLayout.createSequentialGroup()
                .addComponent(jPanelSubVender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelVendaManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMelhorBid)
                    .addComponent(jTextFieldMelhorBid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelVendaManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelVenderQuantidade)
                    .addComponent(jTextFieldVenderQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelVendaManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelVenderTotal)
                    .addComponent(jTextFieldVenderTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addGroup(jPanelVendaManualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonVenderManual)
                    .addComponent(jLabelFeeVenda)
                    .addComponent(jButtonVenderManualTudo))
                .addContainerGap())
        );

        jPanelFundos.setBackground(new java.awt.Color(120, 120, 120));

        jPanelSubFundos.setBackground(new java.awt.Color(35, 35, 35));

        jLabelFundos.setForeground(new java.awt.Color(255, 255, 255));
        jLabelFundos.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelFundos.setText("Funds");

        javax.swing.GroupLayout jPanelSubFundosLayout = new javax.swing.GroupLayout(jPanelSubFundos);
        jPanelSubFundos.setLayout(jPanelSubFundosLayout);
        jPanelSubFundosLayout.setHorizontalGroup(
            jPanelSubFundosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelFundos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelSubFundosLayout.setVerticalGroup(
            jPanelSubFundosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelFundos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
        );

        jLabelMoedaBaseAtual.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMoedaBaseAtual.setText("Current Base:");

        jLabelMoedaCotacaoAtual.setForeground(new java.awt.Color(255, 255, 255));
        jLabelMoedaCotacaoAtual.setText("Current Quote:");

        jTextFieldMoedaBaseAtual.setEditable(false);
        jTextFieldMoedaBaseAtual.setBackground(new java.awt.Color(120, 120, 120));
        jTextFieldMoedaBaseAtual.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldMoedaBaseAtual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jTextFieldMoedaCotacaoAtual.setEditable(false);
        jTextFieldMoedaCotacaoAtual.setBackground(new java.awt.Color(120, 120, 120));
        jTextFieldMoedaCotacaoAtual.setForeground(new java.awt.Color(255, 255, 255));
        jTextFieldMoedaCotacaoAtual.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jButtonAlterarFundos.setText("Deposits and Withdrawals");
        jButtonAlterarFundos.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAlterarFundosActionPerformed(evt);
            }
        });

        jLabelTotalFundos.setFont(new java.awt.Font("Lucida Grande", 2, 12)); // NOI18N
        jLabelTotalFundos.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTotalFundos.setText("Total (Quote Value):");

        javax.swing.GroupLayout jPanelFundosLayout = new javax.swing.GroupLayout(jPanelFundos);
        jPanelFundos.setLayout(jPanelFundosLayout);
        jPanelFundosLayout.setHorizontalGroup(
            jPanelFundosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelSubFundos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelFundosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFundosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonAlterarFundos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFundosLayout.createSequentialGroup()
                        .addGroup(jPanelFundosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelMoedaBaseAtual)
                            .addComponent(jLabelMoedaCotacaoAtual))
                        .addGap(23, 23, 23)
                        .addGroup(jPanelFundosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldMoedaBaseAtual)
                            .addComponent(jTextFieldMoedaCotacaoAtual)))
                    .addComponent(jLabelTotalFundos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelFundosLayout.setVerticalGroup(
            jPanelFundosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFundosLayout.createSequentialGroup()
                .addComponent(jPanelSubFundos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelFundosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMoedaBaseAtual)
                    .addComponent(jTextFieldMoedaBaseAtual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelFundosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMoedaCotacaoAtual)
                    .addComponent(jTextFieldMoedaCotacaoAtual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelTotalFundos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonAlterarFundos)
                .addContainerGap())
        );

        jPanelTransacoes.setBackground(new java.awt.Color(120, 120, 120));

        jPanelHeaderTransacoes.setBackground(new java.awt.Color(55, 55, 55));

        jPanelSub.setBackground(new java.awt.Color(55, 55, 55));

        jLabelID.setForeground(new java.awt.Color(255, 255, 255));
        jLabelID.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelID.setText("ID");

        jLabelTipoTransacao.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTipoTransacao.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTipoTransacao.setText("Type");

        jLabelPrecoTransacao.setForeground(new java.awt.Color(255, 255, 255));
        jLabelPrecoTransacao.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelPrecoTransacao.setText("Price");

        jLabelQuantidadeTransacao.setForeground(new java.awt.Color(255, 255, 255));
        jLabelQuantidadeTransacao.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelQuantidadeTransacao.setText("Amount");

        jLabelTimestampTransacao.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTimestampTransacao.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTimestampTransacao.setText("Timestamp");

        javax.swing.GroupLayout jPanelSubLayout = new javax.swing.GroupLayout(jPanelSub);
        jPanelSub.setLayout(jPanelSubLayout);
        jPanelSubLayout.setHorizontalGroup(
            jPanelSubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSubLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelID, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelTipoTransacao, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelPrecoTransacao, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelQuantidadeTransacao, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelTimestampTransacao, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanelSubLayout.setVerticalGroup(
            jPanelSubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelSubLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelSubLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelID, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPrecoTransacao, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelTipoTransacao, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelTimestampTransacao, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelQuantidadeTransacao, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelHeaderTransacoesLayout = new javax.swing.GroupLayout(jPanelHeaderTransacoes);
        jPanelHeaderTransacoes.setLayout(jPanelHeaderTransacoesLayout);
        jPanelHeaderTransacoesLayout.setHorizontalGroup(
            jPanelHeaderTransacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelSub, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelHeaderTransacoesLayout.setVerticalGroup(
            jPanelHeaderTransacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelSub, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanelSubTransacoes.setBackground(new java.awt.Color(35, 35, 35));

        jLabelTransacoes.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTransacoes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTransacoes.setText("Transactions");

        javax.swing.GroupLayout jPanelSubTransacoesLayout = new javax.swing.GroupLayout(jPanelSubTransacoes);
        jPanelSubTransacoes.setLayout(jPanelSubTransacoesLayout);
        jPanelSubTransacoesLayout.setHorizontalGroup(
            jPanelSubTransacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTransacoes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 964, Short.MAX_VALUE)
        );
        jPanelSubTransacoesLayout.setVerticalGroup(
            jPanelSubTransacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTransacoes, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
        );

        jPanelLinhasTransacoes.setBackground(new java.awt.Color(120, 120, 120));

        javax.swing.GroupLayout jPanelLinhasTransacoesLayout = new javax.swing.GroupLayout(jPanelLinhasTransacoes);
        jPanelLinhasTransacoes.setLayout(jPanelLinhasTransacoesLayout);
        jPanelLinhasTransacoesLayout.setHorizontalGroup(
            jPanelLinhasTransacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1119, Short.MAX_VALUE)
        );
        jPanelLinhasTransacoesLayout.setVerticalGroup(
            jPanelLinhasTransacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 379, Short.MAX_VALUE)
        );

        jScrollPaneLinhasTransacoes.setViewportView(jPanelLinhasTransacoes);

        javax.swing.GroupLayout jPanelTransacoesLayout = new javax.swing.GroupLayout(jPanelTransacoes);
        jPanelTransacoes.setLayout(jPanelTransacoesLayout);
        jPanelTransacoesLayout.setHorizontalGroup(
            jPanelTransacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelSubTransacoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelTransacoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTransacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelHeaderTransacoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPaneLinhasTransacoes, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanelTransacoesLayout.setVerticalGroup(
            jPanelTransacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTransacoesLayout.createSequentialGroup()
                .addComponent(jPanelSubTransacoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelHeaderTransacoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneLinhasTransacoes, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanelTraderbot.setBackground(new java.awt.Color(120, 120, 120));

        jPanelSubTraderbot.setBackground(new java.awt.Color(35, 35, 35));

        jLabelTraderbot.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTraderbot.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTraderbot.setText("Trader Bot Controller");

        javax.swing.GroupLayout jPanelSubTraderbotLayout = new javax.swing.GroupLayout(jPanelSubTraderbot);
        jPanelSubTraderbot.setLayout(jPanelSubTraderbotLayout);
        jPanelSubTraderbotLayout.setHorizontalGroup(
            jPanelSubTraderbotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTraderbot, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelSubTraderbotLayout.setVerticalGroup(
            jPanelSubTraderbotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelTraderbot, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
        );

        jLabelScriptAtualTraderbot.setForeground(new java.awt.Color(255, 255, 255));
        jLabelScriptAtualTraderbot.setText("Trader Script:");

        jComboBoxScriptAtualTraderbot.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jComboBoxScriptAtualTraderbotActionPerformed(evt);
            }
        });

        jButtonAtivarDesativarTrader.setText("Activate");
        jButtonAtivarDesativarTrader.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAtivarDesativarTraderActionPerformed(evt);
            }
        });

        jLabelStatusTrader.setForeground(new java.awt.Color(255, 255, 255));
        jLabelStatusTrader.setText("Status: (not running)");

        jLabelScriptAtualTraderbot1.setForeground(new java.awt.Color(255, 255, 255));
        jLabelScriptAtualTraderbot1.setText("Parameters:");

        jTextFieldParametrosTraderbot.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldParametrosTraderbot.addCaretListener(new javax.swing.event.CaretListener()
        {
            public void caretUpdate(javax.swing.event.CaretEvent evt)
            {
                jTextFieldParametrosTraderbotCaretUpdate(evt);
            }
        });

        javax.swing.GroupLayout jPanelTraderbotLayout = new javax.swing.GroupLayout(jPanelTraderbot);
        jPanelTraderbot.setLayout(jPanelTraderbotLayout);
        jPanelTraderbotLayout.setHorizontalGroup(
            jPanelTraderbotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelSubTraderbot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelTraderbotLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelScriptAtualTraderbot)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxScriptAtualTraderbot, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelScriptAtualTraderbot1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldParametrosTraderbot)
                .addGap(18, 18, 18)
                .addComponent(jLabelStatusTrader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAtivarDesativarTrader)
                .addContainerGap())
        );
        jPanelTraderbotLayout.setVerticalGroup(
            jPanelTraderbotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTraderbotLayout.createSequentialGroup()
                .addComponent(jPanelSubTraderbot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelTraderbotLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelScriptAtualTraderbot)
                    .addComponent(jComboBoxScriptAtualTraderbot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAtivarDesativarTrader)
                    .addComponent(jLabelStatusTrader)
                    .addComponent(jLabelScriptAtualTraderbot1)
                    .addComponent(jTextFieldParametrosTraderbot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelTransacoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanelCompraManual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelVendaManual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelFundos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanelTraderbot, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelCompraManual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelVendaManual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelFundos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelTransacoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelTraderbot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAlterarFundosActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAlterarFundosActionPerformed
    {//GEN-HEADEREND:event_jButtonAlterarFundosActionPerformed
        frames.analisadorasset.offlinetrader.adicionarremoverfundos mfarf = new frames.analisadorasset.offlinetrader.adicionarremoverfundos(this);
        mfarf.show();
    }//GEN-LAST:event_jButtonAlterarFundosActionPerformed

    private void jComboBoxScriptAtualTraderbotActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxScriptAtualTraderbotActionPerformed
    {//GEN-HEADEREND:event_jComboBoxScriptAtualTraderbotActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxScriptAtualTraderbotActionPerformed

    private void jTextFieldMelhorAskActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jTextFieldMelhorAskActionPerformed
    {//GEN-HEADEREND:event_jTextFieldMelhorAskActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldMelhorAskActionPerformed

    private void jButtonComprarManualActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonComprarManualActionPerformed
    {//GEN-HEADEREND:event_jButtonComprarManualActionPerformed
        realizarcompra(Double.valueOf(jTextFieldComprarQuantidade.getText()),true);
    }//GEN-LAST:event_jButtonComprarManualActionPerformed

    private void jTextFieldComprarQuantidadeCaretUpdate(javax.swing.event.CaretEvent evt)//GEN-FIRST:event_jTextFieldComprarQuantidadeCaretUpdate
    {//GEN-HEADEREND:event_jTextFieldComprarQuantidadeCaretUpdate
        atualizarvalortotalprevisaocompra();
    }//GEN-LAST:event_jTextFieldComprarQuantidadeCaretUpdate

    private void jTextFieldVenderQuantidadeCaretUpdate(javax.swing.event.CaretEvent evt)//GEN-FIRST:event_jTextFieldVenderQuantidadeCaretUpdate
    {//GEN-HEADEREND:event_jTextFieldVenderQuantidadeCaretUpdate
        atualizarvalortotalprevisaovenda();
    }//GEN-LAST:event_jTextFieldVenderQuantidadeCaretUpdate

    private void jButtonVenderManualActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonVenderManualActionPerformed
    {//GEN-HEADEREND:event_jButtonVenderManualActionPerformed
        realizarcompra(Double.valueOf(jTextFieldVenderQuantidade.getText()),true);
    }//GEN-LAST:event_jButtonVenderManualActionPerformed

    private void jTextFieldParametrosTraderbotCaretUpdate(javax.swing.event.CaretEvent evt)//GEN-FIRST:event_jTextFieldParametrosTraderbotCaretUpdate
    {//GEN-HEADEREND:event_jTextFieldParametrosTraderbotCaretUpdate
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldParametrosTraderbotCaretUpdate

    private void jButtonAtivarDesativarTraderActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAtivarDesativarTraderActionPerformed
    {//GEN-HEADEREND:event_jButtonAtivarDesativarTraderActionPerformed
        ativardesativartraderbot();
    }//GEN-LAST:event_jButtonAtivarDesativarTraderActionPerformed

    private void jButtonVenderManualTudoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonVenderManualTudoActionPerformed
    {//GEN-HEADEREND:event_jButtonVenderManualTudoActionPerformed
        realizarvendatudo(true);
    }//GEN-LAST:event_jButtonVenderManualTudoActionPerformed

    private void jButtonComprarManualTudoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonComprarManualTudoActionPerformed
    {//GEN-HEADEREND:event_jButtonComprarManualTudoActionPerformed
        realizarcompratudo(true);
    }//GEN-LAST:event_jButtonComprarManualTudoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAlterarFundos;
    private javax.swing.JButton jButtonAtivarDesativarTrader;
    private javax.swing.JButton jButtonComprarManual;
    private javax.swing.JButton jButtonComprarManualTudo;
    private javax.swing.JButton jButtonVenderManual;
    private javax.swing.JButton jButtonVenderManualTudo;
    private javax.swing.JComboBox<String> jComboBoxScriptAtualTraderbot;
    private javax.swing.JLabel jLabelComprar;
    private javax.swing.JLabel jLabelComprarQuantidade;
    private javax.swing.JLabel jLabelComprarTotal;
    private javax.swing.JLabel jLabelFeeCompra;
    private javax.swing.JLabel jLabelFeeVenda;
    private javax.swing.JLabel jLabelFundos;
    public javax.swing.JLabel jLabelID;
    private javax.swing.JLabel jLabelMelhorAsk;
    private javax.swing.JLabel jLabelMelhorBid;
    private javax.swing.JLabel jLabelMoedaBaseAtual;
    private javax.swing.JLabel jLabelMoedaCotacaoAtual;
    public javax.swing.JLabel jLabelPrecoTransacao;
    public javax.swing.JLabel jLabelQuantidadeTransacao;
    private javax.swing.JLabel jLabelScriptAtualTraderbot;
    private javax.swing.JLabel jLabelScriptAtualTraderbot1;
    private javax.swing.JLabel jLabelStatusTrader;
    public javax.swing.JLabel jLabelTimestampTransacao;
    public javax.swing.JLabel jLabelTipoTransacao;
    private javax.swing.JLabel jLabelTotalFundos;
    private javax.swing.JLabel jLabelTraderbot;
    private javax.swing.JLabel jLabelTransacoes;
    private javax.swing.JLabel jLabelVender;
    private javax.swing.JLabel jLabelVenderQuantidade;
    private javax.swing.JLabel jLabelVenderTotal;
    private javax.swing.JPanel jPanelCompraManual;
    private javax.swing.JPanel jPanelFundos;
    private javax.swing.JPanel jPanelHeaderTransacoes;
    private javax.swing.JPanel jPanelLinhasTransacoes;
    private javax.swing.JPanel jPanelSub;
    private javax.swing.JPanel jPanelSubComprar;
    private javax.swing.JPanel jPanelSubFundos;
    private javax.swing.JPanel jPanelSubTraderbot;
    private javax.swing.JPanel jPanelSubTransacoes;
    private javax.swing.JPanel jPanelSubVender;
    private javax.swing.JPanel jPanelTraderbot;
    private javax.swing.JPanel jPanelTransacoes;
    private javax.swing.JPanel jPanelVendaManual;
    private javax.swing.JScrollPane jScrollPaneLinhasTransacoes;
    private javax.swing.JTextField jTextFieldComprarQuantidade;
    private javax.swing.JTextField jTextFieldComprarTotal;
    private javax.swing.JTextField jTextFieldMelhorAsk;
    private javax.swing.JTextField jTextFieldMelhorBid;
    private javax.swing.JTextField jTextFieldMoedaBaseAtual;
    private javax.swing.JTextField jTextFieldMoedaCotacaoAtual;
    private javax.swing.JTextField jTextFieldParametrosTraderbot;
    private javax.swing.JTextField jTextFieldVenderQuantidade;
    private javax.swing.JTextField jTextFieldVenderTotal;
    // End of variables declaration//GEN-END:variables
}
