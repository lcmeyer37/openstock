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
package panels.analisadorasset;

import java.awt.Color;
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
public class analisadorasset extends javax.swing.JPanel
{
    /*
    classe que segura os subitens do analisador de asset, ateh o momento estes itens sao
    grafico (para analise tecnica)
    trader, traderbot e interface para trades offlines
    */
    
    // <editor-fold defaultstate="collapsed" desc="Variáveis Públicas">
    //item analisador de asset ao qual este analisador esta associado
    public panels.analisadorasset.itemanalisadorasset iaassetpai;
    //submobulo grafico
    public panels.analisadorasset.grafico.submodulografico subgrafico;
    //submodulo trader
    public panels.analisadorasset.offlinetrader.submoduloofflinetrader subtrader;
    //recebe o simbolo deste asset
    public String assetsimbolo;
    //</editor-fold>
 
    // <editor-fold defaultstate="collapsed" desc="CONSTRUTOR">
    public analisadorasset(panels.analisadorasset.itemanalisadorasset iaapai)
    {
        initComponents();
        
        inicializar(iaapai);
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Inicialização, Atualização do Asset e Timer">
    void inicializar(panels.analisadorasset.itemanalisadorasset iaapai)
    {
        //setar panel para mostrar submodulos
        jPanelSubmodulosHolder.setLayout(new java.awt.GridLayout(1,1));
        
        //associar o item analisador asset a este analisador asset
        iaassetpai = iaapai;
        assetsimbolo = "testdata";
        
        //criar submodulos grafico e trader para uso
        subgrafico = new panels.analisadorasset.grafico.submodulografico(this);
        subtrader = new panels.analisadorasset.offlinetrader.submoduloofflinetrader(this);
        
        //mostrar submodulo grafico
        mostrarsubmodulografico();
        
        //popular o timer para atualizacao automatica de dados do asset
        setartimeratualizacao();
    }
    
    //timer para atualizar dados do asset automaticamente
    java.util.TimerTask timeratualizardados;
    java.util.Timer timer_timeratualizardados;
    void setartimeratualizacao()
    {
        String tempotimer = jComboBoxTempoAtualizacao.getSelectedItem().toString();
        
        try
        {
            timeratualizardados.cancel();
            timer_timeratualizardados.cancel();
            timeratualizardados = null;
            timer_timeratualizardados = null;
        }
        catch (Exception ex)
        {}
        

        if (tempotimer.equals("Off") == true)
        {
            //nao ligar timer
            atualizardadosasset();
        }
        else
        {   

            long segundo = 1000L; //1000L = 1000ms = 1s
            long minuto = segundo*60;
            long hora = minuto*60;
            long dia = hora*24;
            long mes = dia*30;
            long ano = mes*12;
            
            long period = mes; //vai ser setado
            if (tempotimer.equals("1 minute") == true)
                period = 1*minuto;
            else if (tempotimer.equals("5 minute") == true)
                period = 5*minuto;
            else if (tempotimer.equals("15 minutes") == true)
                period = 15*minuto;
            else if (tempotimer.equals("30 minutes") == true)
                period = 30*minuto;
            else if (tempotimer.equals("1 hour") == true)
                period = 1*hora;
            else if (tempotimer.equals("3 hours") == true)
                period = 3*hora;
            else if (tempotimer.equals("6 hours") == true)
                period = 6*hora;
            else if (tempotimer.equals("12 hours") == true)
                period = 12*hora;
            else if (tempotimer.equals("1 day") == true)
                period = 1*dia;
            else if (tempotimer.equals("2 days") == true)
                period = 2*dia;
            else if (tempotimer.equals("5 days") == true)
                period = 5*dia;
            else if (tempotimer.equals("15 days") == true)
                period = 15*dia;
            else if (tempotimer.equals("1 month") == true)
                period = 1*mes;
            else if (tempotimer.equals("3 months") == true)
                period = 3*mes;
            else if (tempotimer.equals("6 months") == true)
                period = 6*mes;

            timeratualizardados = new java.util.TimerTask() 
            {
                public void run() 
                {
                    atualizardadosasset();
                }
            };
            timer_timeratualizardados = new java.util.Timer("timeratualizardados");
            //apos atualizar o timer, ja realizar uma atualizacao um segundo depois
            timer_timeratualizardados.scheduleAtFixedRate(timeratualizardados, 100L, period);
        }

    }
             
    public void atualizardadosasset()
    {
        jLabelUltimaAtualizacao.setText("Updated at " + new java.util.Date(System.currentTimeMillis()));
        //funcao para recarregar dados dos submodulos relacionados a este asset
        subgrafico.recarregardadossubmodulografico(false);
        subtrader.recarregardadossubmoduloofflinetrader();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Mostrar Submodulos">
    void mostrarsubmodulografico()
    {
        jPanelSubmodulosHolder.removeAll();
        
        //funcao para mostrar submodulo grafico
        jPanelSubmodulosHolder.add(subgrafico);
        jButtonMostrarGrafico.setForeground(Color.red);
        jButtonMostrarTrader.setForeground(Color.black);
        this.validate();
        this.repaint();
    }
    
    void mostrarsubmodulotrader()
    {
        jPanelSubmodulosHolder.removeAll();
        
        //funcao para mostrar submodulo grafico
        jPanelSubmodulosHolder.add(subtrader);
        jButtonMostrarTrader.setForeground(Color.red);
        jButtonMostrarGrafico.setForeground(Color.black);
        this.validate();
        this.repaint();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Salvar e Carregar Dados do Asset">
    
    public void salvardadosasset()
    {
        //funcao para salvar dados do asset: chart e trader offline
       
        try
        {
            // <editor-fold defaultstate="collapsed" desc="Chart">
            
            //funcao para salvar as configuracoes de
            //simbolo, periodo
            //indicadores
            //anotacoes

            // <editor-fold defaultstate="collapsed" desc="criar subxml com indicadores">
            String subxmlIndicadores = "";
            for (int i = 0; i < subgrafico.jPanelIndicadores.getComponentCount(); i++)
            {
                panels.analisadorasset.grafico.itemindicador miia = (panels.analisadorasset.grafico.itemindicador)subgrafico.jPanelIndicadores.getComponent(i);
                subxmlIndicadores = subxmlIndicadores +
                        "<Indicator>" +
                            "<Name>" + miia.jLabelNomeItemIndicador.getText() + "</Name>" +
                            "<ID>" + miia.id + "</ID>" +
                            "<BCID>" + miia.mcbcindicador.idbcode + "</BCID>" +
                            "<Parameters>" + miia.mcbcindicador.parametrosbcodejs + "</Parameters>" +
                        "</Indicator>";

            }
            // </editor-fold>

            // <editor-fold defaultstate="collapsed" desc="criar subxml com annotations">
            String subxmlAnotacoes = "";
            for (int i = 0; i < subgrafico.jPanelAnotacoes.getComponentCount(); i++)
            {
                panels.analisadorasset.grafico.itemanotacao miaa = (panels.analisadorasset.grafico.itemanotacao)subgrafico.jPanelAnotacoes.getComponent(i);

            String tipoanotacao = miaa.tipoanotacao;
            String anotacaoserializada = "";

            try
            {
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
                oos.writeObject(miaa.subannotationsanotacao);
                oos.close();
                anotacaoserializada = java.util.Base64.getEncoder().encodeToString(baos.toByteArray()); 
            }
            catch (java.io.IOException ex)
            {
                //necessario
            }

            subxmlAnotacoes = subxmlAnotacoes +
                    "<Annotation>" +
                        "<Name>" + miaa.jLabelNomeItemAnotacao.getText() + "</Name>" +
                        "<ID>" + miaa.id + "</ID>" +
                        "<Type>" + miaa.tipoanotacao + "</Type>" +
                        "<Parameters>" + anotacaoserializada + "</Parameters>" +
                    "</Annotation>";
            }
            // </editor-fold>


            String subxmlChart =

                    "<CMainInfo>" +
                            "<Symbol>" + subgrafico.jTextFieldNomeSimbolo.getText() + "</Symbol>" +
                            "<Scale>" + subgrafico.escalagraficoescolhido + "</Scale>" +
                            "<Period>" + subgrafico.jComboBoxPeriodoSimbolo.getSelectedItem().toString() + "</Period>" +
                        "</CMainInfo>" +

                        "<IndicatorsInfo>" +
                            subxmlIndicadores +
                        "</IndicatorsInfo>" +

                        "<AnnotationsInfo>" +
                            subxmlAnotacoes +
                        "</AnnotationsInfo>";

            // </editor-fold>
            
            // <editor-fold defaultstate="collapsed" desc="Trader">
            
            String subxmlTransacoes = "";
            for (int i = 0; i < subtrader.otrader.transacoes.size(); i++)
            {
                mierclasses.mcofflinetransaction transacaoatual = (mierclasses.mcofflinetransaction) subtrader.otrader.transacoes.get(i);
                
                subxmlTransacoes = subxmlTransacoes + 
                        "<Transaction>" +
                            "<ID>" + transacaoatual.idstr + "</ID>" +
                            "<TypeTransaction>" + transacaoatual.tipostr + "</TypeTransaction>" +
                            "<PriceTransaction>" + transacaoatual.preco_tradestr + "</PriceTransaction>" +
                            "<AmountTransaction>" + transacaoatual.quantidadestr + "</AmountTransaction>" +
                            "<TimestamplongTransaction>" + transacaoatual.timestampstr + "</TimestamplongTransaction>" +
                        "</Transaction>";
            }
            
            String subxmlTrader =

                    "<TOMainInfo>" +
                            "<Symbol>" + subtrader.otrader.simbolo + "</Symbol>" +
                            "<BaseCurrencyAmount>" + String.valueOf(subtrader.otrader.quantidademoedabase) + "</BaseCurrencyAmount>" +
                            "<QuoteCurrencyAmount>" + String.valueOf(subtrader.otrader.quantidademoedacotacao) + "</QuoteCurrencyAmount>" +
                        "</TOMainInfo>" +

                        "<TransactionsInfo>" +
                            subxmlTransacoes +
                        "</TransactionsInfo>";
            
            //</editor-fold>
            
            String xmlSave = 
                "<?xml version=\"1.0\"?>" +
                    "<OpenstockAssetSave>" +
                    
                        "<About>" +
                            "<Name>" + subgrafico.aassetpai.iaassetpai.jLabelNomeAnalisadorAsset.getText() + "</Name>" +
                            "<ID>" + subgrafico.aassetpai.iaassetpai.id + "</ID>" +
                        "</About>" +
                    
                        "<Chart>" +
                            subxmlChart +
                        "</Chart>" +
                    
                        "<TraderOffline>" +
                            subxmlTrader +
                        "</TraderOffline>" +
                    
                    "</OpenstockAssetSave>";


            //abrir dialog para criar arquivo de save
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogTitle("Please choose a location and name for the Open Stock save file");

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) 
            {
                java.io.File fileToSave = fileChooser.getSelectedFile();

                java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave + ".ossave", "UTF-8");
                writer.println(xmlSave);
                writer.close();
                mierclasses.mcfuncoeshelper.mostrarmensagem("Asset file saved.");
            }
            
        }
        catch (Exception ex)
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("A problem occurred when saving. Exception: " + ex.getMessage());
        }
        
    }
    
    public void carregardadosasset()
    {
        try
        {
            //abrir janela para selecionar arquivo de save para carregar
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogTitle("Please choose an Open Stock file to load");

            int userSelection = fileChooser.showOpenDialog(this);

            if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) 
            {
                
                java.io.File fileToLoad = null;
                DocumentBuilderFactory dbfactory = null;
                DocumentBuilder dbuilder = null;
                Document document = null;
                try
                {
                    fileToLoad = fileChooser.getSelectedFile();
                    dbfactory = DocumentBuilderFactory.newInstance();
                    dbuilder = dbfactory.newDocumentBuilder();
                    document = dbuilder.parse(fileToLoad);
                }
                catch (Exception ex)
                {
                    mierclasses.mcfuncoeshelper.mostrarmensagem(ex.getMessage());
                }
                
                
                //recarregar nome do item asset associado a este
                iaassetpai.renomearitem(assetsimbolo);
                NodeList listabout = document.getElementsByTagName("About");
                Node itemaboutinfounico = listabout.item(0);
                Element elaboutinfounico = (Element) itemaboutinfounico;
                String nomeasset = elaboutinfounico.getElementsByTagName("Name").item(0).getTextContent();
                String idasset = elaboutinfounico.getElementsByTagName("ID").item(0).getTextContent();
                iaassetpai.jLabelNomeAnalisadorAsset.setText(nomeasset);
                iaassetpai.id = idasset;
                
                // <editor-fold defaultstate="collapsed" desc="Recarregar Chart">
                
                // <editor-fold defaultstate="collapsed" desc="comecar recarregando o submodulo com o nome e simbolo desejado">
                NodeList listchartmaininfo = document.getElementsByTagName("CMainInfo");
                Node itemchartmaininfounico = listchartmaininfo.item(0);
                Element elchartmaininfounico = (Element) itemchartmaininfounico;
                String simbolochart = elchartmaininfounico.getElementsByTagName("Symbol").item(0).getTextContent();
                String periodochart = elchartmaininfounico.getElementsByTagName("Period").item(0).getTextContent();
                String escalachart = elchartmaininfounico.getElementsByTagName("Scale").item(0).getTextContent();
                
                subgrafico.jTextFieldNomeSimbolo.setText(simbolochart);
                subgrafico.atualizaropcoescomboboxperiodo();
                for (int i = 0; i < subgrafico.jComboBoxPeriodoSimbolo.getItemCount(); i++)
                {
                    String textoItemAtual = subgrafico.jComboBoxPeriodoSimbolo.getItemAt(i).toString();
                    if (textoItemAtual.equals(periodochart))
                    {
                        subgrafico.jComboBoxPeriodoSimbolo.setSelectedIndex(i);
                        break;
                    }
                }
                subgrafico.alternartipoescala(escalachart);
                //se faz necessario criar novamente o submodulo grafico ao carregar
                subgrafico.recarregardadossubmodulografico(true);
                // </editor-fold>
                
                // <editor-fold defaultstate="collapsed" desc="recarregar indicadores">
                NodeList listaindicadores = document.getElementsByTagName("Indicator");
                
                for (int i = 0; i < listaindicadores.getLength(); i++)
                {
                    Node nodeindicador = listaindicadores.item(i);
                    Element elindicador = (Element) nodeindicador;
                    String nome_indicador = elindicador.getElementsByTagName("Name").item(0).getTextContent();
                    String id_indicador = elindicador.getElementsByTagName("ID").item(0).getTextContent();
                    String bcid_indicador = elindicador.getElementsByTagName("BCID").item(0).getTextContent();
                    String parametrosbc_indicador = elindicador.getElementsByTagName("Parameters").item(0).getTextContent();
                
                    //public itemindicador(mierpanels.submodulografico mpsmg, String idind, String nome, String idbearcode, String parametrosbearcode)
                    subgrafico.adicionarIndicadorLoad(nome_indicador,id_indicador,bcid_indicador,parametrosbc_indicador);
                }
                //mierclasses.mcfuncoeshelper.mostrarmensagem("carregou indicadores");
                // </editor-fold>
                
                // <editor-fold defaultstate="collapsed" desc="recarregar anotacoes">
                NodeList listaanotacoes = document.getElementsByTagName("Annotation");
                
                for (int i = 0; i < listaanotacoes.getLength(); i++)
                {
                    Node nodeanotacao = listaanotacoes.item(i);
                    Element elanotacao = (Element) nodeanotacao;
                    String nome_anotacao = elanotacao.getElementsByTagName("Name").item(0).getTextContent();
                    String id_anotacao = elanotacao.getElementsByTagName("ID").item(0).getTextContent();
                    String tipo_anotacao = elanotacao.getElementsByTagName("Type").item(0).getTextContent();
                    String parameters_anotacao = elanotacao.getElementsByTagName("Parameters").item(0).getTextContent();
                    Object objetos_subanotacoes_anotacao = null;
                    java.util.List<org.jfree.chart.annotations.XYAnnotation> listasubanotacoesdaanotacao = null;
                    try
                    {
                        byte [] data = java.util.Base64.getDecoder().decode(parameters_anotacao);
                        java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(data));
                        Object o = ois.readObject();
                        ois.close();
                        
                        objetos_subanotacoes_anotacao = o;
                        listasubanotacoesdaanotacao = (java.util.List<org.jfree.chart.annotations.XYAnnotation>)objetos_subanotacoes_anotacao;
                    }
                    catch (Exception ex)
                    {
                        //necessario
                    }
                    
                    subgrafico.adicionarAnotacaoLoad(nome_anotacao,id_anotacao,tipo_anotacao,listasubanotacoesdaanotacao);
                }
                //mierclasses.mcfuncoeshelper.mostrarmensagem("carregou anotacoes");
                // </editor-fold>
                
                //</editor-fold>
                
                // <editor-fold defaultstate="collapsed" desc="Recarregar Trader">
                NodeList listotradermaininfo = document.getElementsByTagName("TOMainInfo");
                Node itemotradermaininfounico = listotradermaininfo.item(0);
                Element elotradermaininfounico = (Element) itemotradermaininfounico;
                String simbolootrader = elotradermaininfounico.getElementsByTagName("Symbol").item(0).getTextContent();
                String quantidademoedabase = elotradermaininfounico.getElementsByTagName("BaseCurrencyAmount").item(0).getTextContent();
                String quantidademoedacotacao = elotradermaininfounico.getElementsByTagName("QuoteCurrencyAmount").item(0).getTextContent();
                
                subtrader.otrader.simbolo = simbolootrader;
                subtrader.otrader.quantidademoedabase = Double.valueOf(quantidademoedabase);
                subtrader.otrader.quantidademoedacotacao = Double.valueOf(quantidademoedacotacao);
                
                
                // <editor-fold defaultstate="collapsed" desc="recarregar transacoes">
                NodeList listatransacoes = document.getElementsByTagName("Transaction");
                
                for (int i = 0; i < listatransacoes.getLength(); i++)
                {
                    Node nodetransacao = listatransacoes.item(i);
                    Element eltransacao = (Element) nodetransacao;
                    String id_transacao = eltransacao.getElementsByTagName("ID").item(0).getTextContent();
                    String tipo_transacao = eltransacao.getElementsByTagName("TypeTransaction").item(0).getTextContent();
                    String preco_transacao = eltransacao.getElementsByTagName("PriceTransaction").item(0).getTextContent();
                    String quantidade_transacao = eltransacao.getElementsByTagName("AmountTransaction").item(0).getTextContent();
                    String timestamplongstr_transacao = eltransacao.getElementsByTagName("TimestamplongTransaction").item(0).getTextContent();
                
                    //public itemindicador(mierpanels.submodulografico mpsmg, String idind, String nome, String idbearcode, String parametrosbearcode)
                    //subgrafico.adicionarIndicadorLoad(nome_indicador,id_indicador,bcid_indicador,parametrosbc_indicador);
                    
                    mierclasses.mcofflinetransaction mcotransacaoadicionar = new mierclasses.mcofflinetransaction
                        (id_transacao, tipo_transacao, preco_transacao, quantidade_transacao, timestamplongstr_transacao);
                    
                    subtrader.otrader.transacoes.add(mcotransacaoadicionar);
                }
                // </editor-fold>
                
                
                subtrader.recarregardadossubmoduloofflinetrader();
                //</editor-fold>
            }
        }
        catch (Exception ex)
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("A problem occurred when loading. Exception: " + ex.getMessage());
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

        jPanelEscolherSubmodulo = new javax.swing.JPanel();
        jButtonMostrarGrafico = new javax.swing.JButton();
        jButtonMostrarTrader = new javax.swing.JButton();
        jPanelSubmodulosHolder = new javax.swing.JPanel();
        jLabelTimer = new javax.swing.JLabel();
        jComboBoxTempoAtualizacao = new javax.swing.JComboBox<>();
        jLabelTempoAtualizacao = new javax.swing.JLabel();
        jLabelUltimaAtualizacao = new javax.swing.JLabel();

        jPanelEscolherSubmodulo.setBackground(new java.awt.Color(25, 25, 25));

        jButtonMostrarGrafico.setForeground(new java.awt.Color(255, 0, 0));
        jButtonMostrarGrafico.setText("Chart");
        jButtonMostrarGrafico.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonMostrarGraficoActionPerformed(evt);
            }
        });

        jButtonMostrarTrader.setText("Offline Trader");
        jButtonMostrarTrader.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonMostrarTraderActionPerformed(evt);
            }
        });

        jPanelSubmodulosHolder.setBackground(new java.awt.Color(255, 0, 0));

        javax.swing.GroupLayout jPanelSubmodulosHolderLayout = new javax.swing.GroupLayout(jPanelSubmodulosHolder);
        jPanelSubmodulosHolder.setLayout(jPanelSubmodulosHolderLayout);
        jPanelSubmodulosHolderLayout.setHorizontalGroup(
            jPanelSubmodulosHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelSubmodulosHolderLayout.setVerticalGroup(
            jPanelSubmodulosHolderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 404, Short.MAX_VALUE)
        );

        jLabelTimer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/timer.png"))); // NOI18N

        jComboBoxTempoAtualizacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Off", "1 minute", "5 minutes", "15 minutes", "30 minutes", "1 hour", "3 hours", "6 hours", "12 hours", "1 day", "2 days", "5 days", "15 days", "1 month", "3 months", "6 months" }));
        jComboBoxTempoAtualizacao.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jComboBoxTempoAtualizacaoActionPerformed(evt);
            }
        });

        jLabelTempoAtualizacao.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTempoAtualizacao.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelTempoAtualizacao.setText("Update Frequency:");

        jLabelUltimaAtualizacao.setForeground(new java.awt.Color(255, 255, 255));
        jLabelUltimaAtualizacao.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelUltimaAtualizacao.setText("Updated at TIME");

        javax.swing.GroupLayout jPanelEscolherSubmoduloLayout = new javax.swing.GroupLayout(jPanelEscolherSubmodulo);
        jPanelEscolherSubmodulo.setLayout(jPanelEscolherSubmoduloLayout);
        jPanelEscolherSubmoduloLayout.setHorizontalGroup(
            jPanelEscolherSubmoduloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelSubmodulosHolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelEscolherSubmoduloLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelEscolherSubmoduloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelEscolherSubmoduloLayout.createSequentialGroup()
                        .addComponent(jButtonMostrarGrafico)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonMostrarTrader)
                        .addGap(213, 447, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelEscolherSubmoduloLayout.createSequentialGroup()
                        .addComponent(jLabelUltimaAtualizacao)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelTimer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTempoAtualizacao)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxTempoAtualizacao, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanelEscolherSubmoduloLayout.setVerticalGroup(
            jPanelEscolherSubmoduloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelEscolherSubmoduloLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelEscolherSubmoduloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonMostrarTrader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonMostrarGrafico))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelSubmodulosHolder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(14, 14, 14)
                .addGroup(jPanelEscolherSubmoduloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelEscolherSubmoduloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBoxTempoAtualizacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelTempoAtualizacao))
                    .addComponent(jLabelTimer)
                    .addComponent(jLabelUltimaAtualizacao))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelEscolherSubmodulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelEscolherSubmodulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonMostrarGraficoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonMostrarGraficoActionPerformed
    {//GEN-HEADEREND:event_jButtonMostrarGraficoActionPerformed
        mostrarsubmodulografico();
    }//GEN-LAST:event_jButtonMostrarGraficoActionPerformed

    private void jButtonMostrarTraderActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonMostrarTraderActionPerformed
    {//GEN-HEADEREND:event_jButtonMostrarTraderActionPerformed
       mostrarsubmodulotrader();
    }//GEN-LAST:event_jButtonMostrarTraderActionPerformed

    private void jComboBoxTempoAtualizacaoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jComboBoxTempoAtualizacaoActionPerformed
    {//GEN-HEADEREND:event_jComboBoxTempoAtualizacaoActionPerformed
        setartimeratualizacao();
    }//GEN-LAST:event_jComboBoxTempoAtualizacaoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonMostrarGrafico;
    private javax.swing.JButton jButtonMostrarTrader;
    private javax.swing.JComboBox<String> jComboBoxTempoAtualizacao;
    private javax.swing.JLabel jLabelTempoAtualizacao;
    private javax.swing.JLabel jLabelTimer;
    private javax.swing.JLabel jLabelUltimaAtualizacao;
    private javax.swing.JPanel jPanelEscolherSubmodulo;
    private javax.swing.JPanel jPanelSubmodulosHolder;
    // End of variables declaration//GEN-END:variables
}
