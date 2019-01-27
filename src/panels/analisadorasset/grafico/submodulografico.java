/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package panels.analisadorasset.grafico;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import mierclasses.mcfuncoeshelper;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

/**
 *
 * @author lucasmeyer
 */
public class submodulografico extends javax.swing.JPanel
{
    //jpanel que vai conter um grafico para analise

    public panels.analisadorasset.analisadorasset aassetpai; //analisador de asset pai, contem modulos para analises do asset (ateh o momento grafico e trader)
    
    public mierclasses.mcchartgenerator mcg; //classe utilizada para desenhar graficos
    
    Boolean poderecarregar = false; //diz se o submodulo ja foi criado pela primeira vez, e pode ser recarregado
    
    // <editor-fold defaultstate="collapsed" desc="Construtores Submodulo Grafico">
    
    //construtor novo submodulo grafico
    public submodulografico(panels.analisadorasset.analisadorasset aapai)
    {
        initComponents();
        
        aassetpai = aapai;
        
        inicializarsubmodulografico();

    }
    
    //inicializacao de novo submodulo grafico
    void inicializarsubmodulografico()
    {
        //popular objeto utilizado para desenho de graficos
        mcg = new mierclasses.mcchartgenerator();
        
        jPanelAnotacoes.setLayout(new java.awt.GridLayout(100,1));
        jPanelIndicadores.setLayout(new java.awt.GridLayout(100,1));
        
        //comecar mostrando dataset de teste
        jTextFieldNomeSimbolo.setText("testdata");
        criargraficoohlc();
        poderecarregar = true;
        
        //splitter nao aparece quando nao tem grafico secundario
        jSplitPaneChartpanels.setDividerLocation(500);
        
        //timer e timertask para atualizar range e info de graficos utilizados
        java.util.TimerTask chartrangeinfoupdater = new java.util.TimerTask() 
        {
            public void run() 
            {
                atualizarinformacoesposicaoatualgrafico();
                atualizarrangegraficosecundario(); 
            }
        };
        java.util.Timer timer_chartrangeinfoupdater = new java.util.Timer("chartrangeinfoupdater");
        long delay  = 100L;
        long period = 100L;
        timer_chartrangeinfoupdater.scheduleAtFixedRate(chartrangeinfoupdater, delay, period);
    
        this.validate();
        this.repaint();
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Main Section">
    
    public void adicionarsimboloaotextboxsubmodulo(String simboloadicionar)
    {
        //funcao para adicionar o codigo do simbolo no textbox de simbolo
        jTextFieldNomeSimbolo.setText(simboloadicionar);
    }
    
    
    //funcao responsavel por criar o grafico com o simbolo e periodo desejado pela primeira vez
    public void criargraficoohlc()
    {
        //receber informacoes de candles de acordo com os paremetros principais
        String simboloescolhido = jTextFieldNomeSimbolo.getText();
        String periodoescolhido = jComboBoxPeriodoSimbolo.getSelectedItem().toString();
        String escalagrafico = escalagraficoescolhido;
        
        
        java.util.List<mierclasses.mccandle> candles = null;
        
        if ((simboloescolhido.equals("testdata")) == true)
        {
            //codigo para criar um dataset offline para teste
            candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartsample();
        }
        else if ((simboloescolhido.equals("testdata")) == false)
        {
            if (periodoescolhido.equals("1 Day"))
                candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartwithminutes(simboloescolhido, "1d");
            else if (periodoescolhido.equals("1 Month"))
                candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartwithoutminutes(simboloescolhido, "1m");
            else if (periodoescolhido.equals("3 Months"))
                candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartwithoutminutes(simboloescolhido, "3m");
            else if (periodoescolhido.equals("6 Months"))
                candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartwithoutminutes(simboloescolhido, "6m");
            else if (periodoescolhido.equals("Year-to-date"))
                candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartwithoutminutes(simboloescolhido, "ytd");
            else if (periodoescolhido.equals("1 Year"))
                candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartwithoutminutes(simboloescolhido, "1y");
            else if (periodoescolhido.equals("2 Years"))
                candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartwithoutminutes(simboloescolhido, "2y");
            else if (periodoescolhido.equals("5 Years"))
                candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartwithoutminutes(simboloescolhido, "5y");
        }
        
        
        //recriar grafico OHLC resetando anotacoes e indicadores atuais
        mcg.recriarohlc(candles,simboloescolhido + " (" + periodoescolhido + ")",escalagrafico);
        //receber cpanel OHLC pos-atualizacao para adicionar ao panel
        org.jfree.chart.ChartPanel chartpanel = mcg.retornarcpanelohlc();
        chartpanel.addChartMouseListener(new org.jfree.chart.ChartMouseListener()
        {
            public void chartMouseClicked(org.jfree.chart.ChartMouseEvent e)
            {
                interpretarmouseclickchart(e);
            }

            public void chartMouseMoved(org.jfree.chart.ChartMouseEvent e) 
            {
                interpretarmousemovechart(e);
            }
        });
        
        //mostrar cpanel OHLC (deletar qualquer outro panel antes presente)
        jPanelIndicadores.removeAll();
        jPanelAnotacoes.removeAll();
        jPanelOHLCChartpanel.removeAll();
        jPanelOHLCChartpanel.setLayout(new java.awt.BorderLayout());
        jPanelOHLCChartpanel.add(chartpanel);
        //setVisible(true);
        this.validate();
    }
    
    public void salvarconfiguracaoasset()
    {
        try
        {
                    //funcao para salvar as configuracoes de
        //simbolo, periodo
        //indicadores
        //anotacoes
        
        // <editor-fold defaultstate="collapsed" desc="criar subxml com indicadores">
        String subxmlIndicadores = "";
        for (int i = 0; i < jPanelIndicadores.getComponentCount(); i++)
        {
                panels.analisadorasset.grafico.itemindicador miia = (panels.analisadorasset.grafico.itemindicador)jPanelIndicadores.getComponent(i);
            subxmlIndicadores = subxmlIndicadores +
                    "<Indicator>" +
                        "<Name>" + miia.jLabelNomeItemIndicador.getText() + "</Name>" +
                        "<ID>" + miia.id + "</ID>" +
                        "<BCID>" + miia.mbcodeinterpreter.idbcode + "</BCID>" +
                        "<Parameters>" + miia.mbcodeinterpreter.parametrosbcodejs + "</Parameters>" +
                    "</Indicator>";
            
        }
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="criar subxml com annotations">
        String subxmlAnotacoes = "";
        for (int i = 0; i < jPanelAnotacoes.getComponentCount(); i++)
        {
                panels.analisadorasset.grafico.itemanotacao miaa = (panels.analisadorasset.grafico.itemanotacao)jPanelAnotacoes.getComponent(i);
            
            String tipoanotacao = miaa.tipoanotacao;
            String anotacaoserializada = "";
            
            //if (tipoanotacao.equals("line"))
            //{
                //org.jfree.chart.annotations.XYLineAnnotation lineserialize = 
                //        (org.jfree.chart.annotations.XYLineAnnotation) miaa.annotation;
                
                //making serialization of the annotations
                //https://www.tutorialspoint.com/java/java_serialization.htm
                //https://stackoverflow.com/questions/134492/how-to-serialize-an-object-into-a-string
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
            //}
            
            
            
            subxmlAnotacoes = subxmlAnotacoes +
                    "<Annotation>" +
                        "<Name>" + miaa.jLabelNomeItemAnotacao.getText() + "</Name>" +
                        "<ID>" + miaa.id + "</ID>" +
                        "<Type>" + miaa.tipoanotacao + "</Type>" +
                        "<Parameters>" + anotacaoserializada + "</Parameters>" +
                    "</Annotation>";
        }
        // </editor-fold>
        
        
        String xmlSalvar =
                "<?xml version=\"1.0\"?>" +
                    "<OpenstockAssetSave>" +
                        
                        "<MainInfo>" +
                            "<Name>" + aassetpai.iaassetpai.jLabelNomeAnalisadorAsset.getText() + "</Name>" +
                            "<ID>" + aassetpai.iaassetpai.id + "</ID>" +
                            "<Symbol>" + jTextFieldNomeSimbolo.getText() + "</Symbol>" +
                            "<Scale>" + escalagraficoescolhido + "</Scale>" +
                            "<Period>" + jComboBoxPeriodoSimbolo.getSelectedItem().toString() + "</Period>" +
                        "</MainInfo>" +
                
                        "<IndicatorsInfo>" +
                            subxmlIndicadores +
                        "</IndicatorsInfo>" +
                
                        "<AnnotationsInfo>" +
                            subxmlAnotacoes +
                        "</AnnotationsInfo>" +
                
                    "</OpenstockAssetSave>";
        
            //abrir dialog para criar arquivo de save
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogTitle("Please choose a location and name for the Open Stock save file");

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) 
            {
                java.io.File fileToSave = fileChooser.getSelectedFile();

                java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave + ".ossave", "UTF-8");
                writer.println(xmlSalvar);
                writer.close();
            }
            
            mierclasses.mcfuncoeshelper.mostrarmensagem("Asset file saved.");
        }
        catch (Exception ex)
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("A problem occurred when saving. Exception: " + ex.getMessage());
        }

    }
    
    public void carregarconfiguracaoasset()
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
                
                // <editor-fold defaultstate="collapsed" desc="comecar recarregando o submodulo com o nome e simbolo desejado">
                NodeList listmaininfo = document.getElementsByTagName("MainInfo");
                Node itemmaininfounico = listmaininfo.item(0);
                Element elmaininfounico = (Element) itemmaininfounico;
                String nomeasset = elmaininfounico.getElementsByTagName("Name").item(0).getTextContent();
                String iditemgrafico = elmaininfounico.getElementsByTagName("ID").item(0).getTextContent();
                String simbolo = elmaininfounico.getElementsByTagName("Symbol").item(0).getTextContent();
                String periodo = elmaininfounico.getElementsByTagName("Period").item(0).getTextContent();
                String escala = elmaininfounico.getElementsByTagName("Scale").item(0).getTextContent();
                
                aassetpai.iaassetpai.jLabelNomeAnalisadorAsset.setText(nomeasset);
                aassetpai.iaassetpai.id = iditemgrafico;
                jTextFieldNomeSimbolo.setText(simbolo);
                for (int i = 0; i < jComboBoxPeriodoSimbolo.getItemCount(); i++)
                {
                    String textoItemAtual = jComboBoxPeriodoSimbolo.getItemAt(i).toString();
                    if (textoItemAtual.equals(periodo))
                    {
                        jComboBoxPeriodoSimbolo.setSelectedIndex(i);
                        break;
                    }
                }
                alternartipoescala(escala);
                criargraficoohlc();
                //mierclasses.mcfuncoeshelper.mostrarmensagem("carregou grafico");
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
                    adicionarIndicadorLoad(nome_indicador,id_indicador,bcid_indicador,parametrosbc_indicador);
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
                    
                    adicionarAnotacaoLoad(nome_anotacao,id_anotacao,tipo_anotacao,listasubanotacoesdaanotacao);
                }
                //mierclasses.mcfuncoeshelper.mostrarmensagem("carregou anotacoes");
                // </editor-fold>
                
                poderecarregar = true;
            }
        }
        catch (Exception ex)
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("A problem occurred when loading. Exception: " + ex.getMessage());
        }

    }
    
    String escalagraficoescolhido = "linear";
    void alternartipoescala(String escalaalternar)
    {
        if (escalaalternar.equals("linear"))
        {
            escalagraficoescolhido = "linear";
            jLabelLinearSwitch.setForeground(Color.blue);
            jLabelLogaritmicaSwitch.setForeground(Color.white);
        }
        else if (escalaalternar.equals("logaritmica"))
        {
            escalagraficoescolhido = "logaritmica";
            jLabelLinearSwitch.setForeground(Color.white);
            jLabelLogaritmicaSwitch.setForeground(Color.blue);
        }
    }
    

    public void recarregardadosasset()
    {
        //funcao para recarregar dados ohlc e de indicadores
        //e recarregar os desenhos graficos no chart OHLC
        
        // <editor-fold defaultstate="collapsed" desc="Recarregar Chartpanel OHLC">
        
        //receber informacoes de candles de acordo com os paremetros principais
        String simboloescolhido = jTextFieldNomeSimbolo.getText();
        String periodoescolhido = jComboBoxPeriodoSimbolo.getSelectedItem().toString();
        String escalagrafico = escalagraficoescolhido;
        
        
        java.util.List<mierclasses.mccandle> candles = null;
        
        if ((simboloescolhido.equals("testdata")) == true)
        {
            //codigo para criar um dataset offline para teste
            candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartsample();
        }
        else if ((simboloescolhido.equals("testdata")) == false)
        {
            if (periodoescolhido.equals("1 Day"))
                candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartwithminutes(simboloescolhido, "1d");
            else if (periodoescolhido.equals("1 Month"))
                candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartwithoutminutes(simboloescolhido, "1m");
            else if (periodoescolhido.equals("3 Months"))
                candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartwithoutminutes(simboloescolhido, "3m");
            else if (periodoescolhido.equals("6 Months"))
                candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartwithoutminutes(simboloescolhido, "6m");
            else if (periodoescolhido.equals("Year-to-date"))
                candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartwithoutminutes(simboloescolhido, "ytd");
            else if (periodoescolhido.equals("1 Year"))
                candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartwithoutminutes(simboloescolhido, "1y");
            else if (periodoescolhido.equals("2 Years"))
                candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartwithoutminutes(simboloescolhido, "2y");
            else if (periodoescolhido.equals("5 Years"))
                candles = aassetpai.iaassetpai.tprincipalpai.miex.receberstockchartwithoutminutes(simboloescolhido, "5y");
        }
        
        
        //recarregar grafico OHLC
        mcg.recarregarohlc(candles,simboloescolhido + " (" + periodoescolhido + ")",escalagrafico);
        //receber cpanel OHLC pos-atualizacao para adicionar ao panel
        org.jfree.chart.ChartPanel chartpanel = mcg.retornarcpanelohlc();
        chartpanel.addChartMouseListener(new org.jfree.chart.ChartMouseListener()
        {
            public void chartMouseClicked(org.jfree.chart.ChartMouseEvent e)
            {
                interpretarmouseclickchart(e);
            }

            public void chartMouseMoved(org.jfree.chart.ChartMouseEvent e) 
            {
                interpretarmousemovechart(e);
            }
        });
        
        //atualizar cpanel OHLC
        jPanelOHLCChartpanel.removeAll();
        jPanelOHLCChartpanel.setLayout(new java.awt.BorderLayout());
        jPanelOHLCChartpanel.add(chartpanel);

        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="Recarregar Dados e Plots dos Indicadores">
        
        //comecar limpando graficamente os timeseries dos indicadores
        mcg.removerplotohlc_indicadores();
        
        //atualizar dados de todos os indicadores e adiciona-los novamente (os seus ids ja existem)
        for (int i = 0; i < jPanelIndicadores.getComponentCount(); i++)
        {
            
            panels.analisadorasset.grafico.itemindicador novoindicador = (panels.analisadorasset.grafico.itemindicador)jPanelIndicadores.getComponent(i);
            String statusrunindicador = novoindicador.rodarscriptindicador();
            if (statusrunindicador.equals("ok"))
            {
                if (novoindicador.mbcodeinterpreter.tipoplot_lastrun.equals("drawoncandles"))
                {
                    //considerando que o indicador rodou com sucesso, e que ele deve ser desenhado no ohlc,
                    //adicionar dados do indicador no grafico
                    mcg.adicionarplotohlc_indicador
                    (
                        novoindicador.mbcodeinterpreter.pontosx_lastrun,
                        novoindicador.mbcodeinterpreter.pontosy_lastrun,
                        novoindicador.mbcodeinterpreter.tituloscript_lastrun
                    );

                    //recarregar grafico separado do indicador
                    novoindicador.recarregargraficoseparadoindicador();
                }
                else
                {
                    //recarregar grafico separado do indicador
                    novoindicador.recarregargraficoseparadoindicador();
                }
            }
            else
            {
                mierclasses.mcfuncoeshelper.mostrarmensagem("Algum problema ocorreu e o indicador não pôde ser utilizado:\n\n" + statusrunindicador);
            }
            
        }
        
        //tambem recarregar grafico secundario
        for (int i = 0; i < jPanelIndicadores.getComponentCount(); i++)
        {
            panels.analisadorasset.grafico.itemindicador miia = (panels.analisadorasset.grafico.itemindicador)jPanelIndicadores.getComponent(i);
            String indicadorembottom = miia.jLabelEscolherGraficoParaBottom.getText();
            if (indicadorembottom.equals("S"))
            {
                //encontrado o indicador cujo grafico esta como secundario, tambem atualiza-lo
                setarGraficoIndicadorSecundario(miia);
                break;
            }
        }
        
        //</editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="Recarregar Dados e Plots das Anotacoes">
        //remover todos subannotations
        mcg.removerplotohlc_todossubannotations();
        
        for (int i = 0; i < jPanelAnotacoes.getComponentCount(); i++)
        {
            panels.analisadorasset.grafico.itemanotacao novoanotacao = (panels.analisadorasset.grafico.itemanotacao)jPanelAnotacoes.getComponent(i);
            mcg.adicionarplotohlc_subannotationsobjectbase64type(novoanotacao.subannotationsanotacao);
        }
        // </editor-fold>
        
        this.validate();
        this.repaint(); 
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Indicators Section">
        
    public void adicionarIndicadorNovo(String idbc, String paramsbc)
    {
        //funcao para adicionar novo item indicador a este submodulo grafico
        
        //criar item do indicador
        panels.analisadorasset.grafico.itemindicador novoindicador = new panels.analisadorasset.grafico.itemindicador(this, idbc, paramsbc);
        //rodar algoritmo do indicador
        String statusrunindicador = novoindicador.rodarscriptindicador();
        if (statusrunindicador.equals("ok"))
        {
            if (novoindicador.mbcodeinterpreter.tipoplot_lastrun.equals("drawoncandles"))
            {
                //considerando que o indicador rodou com sucesso, e que ele deve ser desenhado no ohlc,
                //adicionar dados do indicador no grafico
                mcg.adicionarplotohlc_indicador
                (
                    novoindicador.mbcodeinterpreter.pontosx_lastrun,
                    novoindicador.mbcodeinterpreter.pontosy_lastrun,
                    novoindicador.mbcodeinterpreter.tituloscript_lastrun
                );
                //adicionar id de controle no chart generator
                mcg.adicionarplotohlc_indicadorid(novoindicador.id);
                
                //o grafco tambem pode ser apresentado separadamente
                novoindicador.criargraficoseparadoindicador();
                novoindicador.recarregargraficoseparadoindicador();
            }
            else
            {
                //considerando que o grafico deve ser desenhado separadamente
                novoindicador.criargraficoseparadoindicador();
                novoindicador.recarregargraficoseparadoindicador();
            }

            
            //adicionar item no submodulo
            jPanelIndicadores.add(novoindicador);
            this.validate();
            this.repaint(); 
        }
        else
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("Algum problema ocorreu e o indicador não pôde ser utilizado:\n\n" + statusrunindicador);
        }

        //mcg.printlistaidsindicador();
    }
    
    public void adicionarIndicadorLoad(String nome, String id, String idbc, String paramsbc)
    {
        panels.analisadorasset.grafico.itemindicador novoindicador = new panels.analisadorasset.grafico.itemindicador(this, id, nome, idbc, paramsbc);
        String statusrunindicador = novoindicador.rodarscriptindicador();
        if (statusrunindicador.equals("ok"))
        {
            if (novoindicador.mbcodeinterpreter.tipoplot_lastrun.equals("drawoncandles"))
            {
                //considerando que o indicador rodou com sucesso, e que ele deve ser desenhado no ohlc,
                //adicionar dados do indicador no grafico
                mcg.adicionarplotohlc_indicador
                (
                    novoindicador.mbcodeinterpreter.pontosx_lastrun,
                    novoindicador.mbcodeinterpreter.pontosy_lastrun,
                    novoindicador.mbcodeinterpreter.tituloscript_lastrun
                );
                mcg.adicionarplotohlc_indicadorid(novoindicador.id);
                
                //o grafco tambem pode ser apresentado separadamente
                novoindicador.criargraficoseparadoindicador();
                novoindicador.recarregargraficoseparadoindicador();
            }
            else
            {
                //considerando que o grafico deve ser desenhado separadamente
                novoindicador.criargraficoseparadoindicador();
                novoindicador.recarregargraficoseparadoindicador();
            }

            jPanelIndicadores.add(novoindicador);
            this.validate();
            this.repaint(); 
        }
        else
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("Algum problema ocorreu e o indicador não pôde ser utilizado:\n\n" + statusrunindicador);
        }

    }

    public void removerIndicador(panels.analisadorasset.grafico.itemindicador mpiiremover)
    {
        if (mpiiremover.mbcodeinterpreter.tipoplot_lastrun.equals("drawoncandles"))
        {
            mcg.removerplotohlc_indicador(mpiiremover.id);
            mcg.removerplotohlc_indicadorid(mpiiremover.id);
        }
        
        jPanelIndicadores.remove(mpiiremover);
        this.validate();
        this.repaint();
        
        //mcg.printlistaidsindicador();
    }

    public void setarGraficoIndicadorSecundario(panels.analisadorasset.grafico.itemindicador mpiidestacarabaixo)
    {
        for (int i = 0; i < jPanelIndicadores.getComponentCount(); i++)
        {
            panels.analisadorasset.grafico.itemindicador miia = (panels.analisadorasset.grafico.itemindicador)jPanelIndicadores.getComponent(i);
            String rootjar = mierclasses.mcfuncoeshelper.retornarpathbaseprograma();
            String caminhoimagem = rootjar + "/outfiles/assets/downgray.png";
            miia.jLabelEscolherGraficoParaBottom.setIcon(new javax.swing.ImageIcon(caminhoimagem));
            miia.chartseparadoembottom = false;
        }
        
        //funcao que pega o grafico separado deste indicador e o separa para ficar logo abaixo do grafico ohlc
        //principal, tambem eh desejavel que quando o usuario estiver realizando zoom no grafico ohlc,
        //que o range do zoom do ohlc tambem passe para este grafico em questao
        jPanelSecondaryChartPanel.removeAll();
        jPanelSecondaryChartPanel.setLayout(new java.awt.BorderLayout());       
        jPanelSecondaryChartPanel.add
        (
            mpiidestacarabaixo.mfcs.retornarnovoplot_indicador
            (
                     mpiidestacarabaixo.mbcodeinterpreter.pontosx_lastrun, 
                     mpiidestacarabaixo.mbcodeinterpreter.pontosy_lastrun, 
                     mpiidestacarabaixo.mbcodeinterpreter.tituloscript_lastrun,
                     mpiidestacarabaixo.mbcodeinterpreter.tipoplot_lastrun
             )       
        );
        String rootjar = mierclasses.mcfuncoeshelper.retornarpathbaseprograma();
        String caminhoimagem = rootjar + "/outfiles/assets/downblue.png";
        mpiidestacarabaixo.jLabelEscolherGraficoParaBottom.setIcon(new javax.swing.ImageIcon(caminhoimagem));
        mpiidestacarabaixo.chartseparadoembottom = true;

        jSplitPaneChartpanels.setDividerLocation(350);
        this.validate();
        this.repaint();
    }
    
    public void removerGraficoIndicadorSecundario()
    {
        jPanelSecondaryChartPanel.removeAll();
        
        for (int i = 0; i < jPanelIndicadores.getComponentCount(); i++)
        {
            panels.analisadorasset.grafico.itemindicador miia = (panels.analisadorasset.grafico.itemindicador)jPanelIndicadores.getComponent(i);
            String rootjar = mierclasses.mcfuncoeshelper.retornarpathbaseprograma();
            String caminhoimagem = rootjar + "/outfiles/assets/downgray.png";
            miia.jLabelEscolherGraficoParaBottom.setIcon(new javax.swing.ImageIcon(caminhoimagem));
            miia.chartseparadoembottom = false;
        }
        jSplitPaneChartpanels.setDividerLocation(500);
        this.validate();
        this.repaint();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Annotations Section">
    
    void adicionarAnotacaoNovo()
    {
        //funcao especial que roda sempre com o mouse click dentro do chart panel, eh necessario calcular a diferenca 
        //entre subannotations presentes no grafico com o numero atual de ids de subannotations do chartgenerator atual,
        //para saber se um novo item de anotacao deve ser adicionado no submodulo
        
        java.util.List<org.jfree.chart.annotations.XYAnnotation> listasubanotacoesgraficas = mcg.retornartodassubanotacoes();
        int tamanhoanotacoesgraficas = listasubanotacoesgraficas.size();
        int quantidadeidssubmoduloatual = mcg.idferramentassubannotationsatual.size();
        
        int diferencasubannotationsgraficoeidssubannotations = tamanhoanotacoesgraficas - quantidadeidssubmoduloatual;
        
        if (diferencasubannotationsgraficoeidssubannotations > 0)
        {
            //considerando que existem mais subannotations graficos que ids na lista, quer dizer que acabou de ser adicionado uma nova anotacao
            //e ela deve ser registrada neste submodulo com um novo item anotacao e seu id adicionado na lista de controle do chart generator
            panels.analisadorasset.grafico.itemanotacao novompia = new panels.analisadorasset.grafico.itemanotacao(this,mcg.ferramentaatualgrafico,mcg.ultimalistasubanotacoesanotacao);
            
            //adicionar esta nova anotacao a lista de controle do chartgenerator atual
             mcg.adicionarplotohlc_ferramentaid(novompia.id, novompia.subannotationsanotacao.size());
            
            //adicionar item de anotacao grafica deste submodulo
            jPanelAnotacoes.add(novompia);    
        }
        
        this.validate();
        this.repaint();
    }
    
    public void adicionarAnotacaoLoad(String nome, String id, String tipo, java.util.List<org.jfree.chart.annotations.XYAnnotation> subanotacoesanotacaoobjeto)
    {
        //funcao para adicionar nova anotacao em modo de carregamento
        
        //criar novo item de anotacao
        panels.analisadorasset.grafico.itemanotacao novompia = new panels.analisadorasset.grafico.itemanotacao(this, nome, id, tipo, subanotacoesanotacaoobjeto);
        //adicionar subannotations referentes a esta anotacao no grafico
        mcg.adicionarplotohlc_subannotationsobjectbase64type(subanotacoesanotacaoobjeto);
        //adicionar esta anotacao a lista de controle do chartgenerator atual
        mcg.adicionarplotohlc_ferramentaid(novompia.id, subanotacoesanotacaoobjeto.size());
        //adicionar item de anotacao grafica deste submodulo
        jPanelAnotacoes.add(novompia);
        
        this.validate();
        this.repaint();
    }
    
    public void removerAnotacao(panels.analisadorasset.grafico.itemanotacao mpiaremover)
    {
        //remover subanotacoes graficas desta anotacao do grafico
        mcg.removerplotohlc_subannotations(mpiaremover.subannotationsanotacao);
        //remover os id referente a esta anotacao da lista de controle
        mcg.removerplotohlc_ferramentaid(mpiaremover.id, mpiaremover.subannotationsanotacao.size());
        //remover item de anotacao grafica deste submodulo
        jPanelAnotacoes.remove(mpiaremover);
        
        this.validate();
        this.repaint();
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Tools and OHLC Section">
    
    // <editor-fold defaultstate="collapsed" desc="Funcoes de interpretacao de mouse events do jfreechart">
    void interpretarmouseclickchart(org.jfree.chart.ChartMouseEvent e)
    {
        adicionarAnotacaoNovo();
    }
    
    void interpretarmousemovechart(org.jfree.chart.ChartMouseEvent e)
    {       
    }
    //</editor-fold>
    
    void resetarcorbotoesferramentas()
    {
        jButtonAtivarSelecao.setForeground(Color.black);
        jButtonAtivarRegua.setForeground(Color.black);
        jButtonAtivarReta.setForeground(Color.black);
        jButtonAtivarFibonacci.setForeground(Color.black);
        jButtonAtivarTexto.setForeground(Color.black);
    }
        
    void atualizarinformacoesposicaoatualgrafico()
    {
        String valoryatualohlc = String.format( "%.4f", mcg.valormouseatualgraficoy);
        java.util.Date dataatualohlc = new java.util.Date((long)mcg.valormouseatualgraficox);
        //String valoratualrangex = String.format( "%.4f", mcg.rangex.getLength());
        //String valoratualrangey = String.format( "%.4f", mcg.rangey.getLength());
        jLabelInfo.setText("Price: " + valoryatualohlc + " Date: " + dataatualohlc.toString());   
    }
    
    void atualizarrangegraficosecundario()
    {
        //funcao para atualizar o range do grafico secundario, para ser igual ao do grafico primario
        
        if (jPanelSecondaryChartPanel.getComponentCount() == 1)
        {
            //org.jfree.chart.ChartPanel chartpanelohlc = mcg.chartpanelatual;
            //org.jfree.chart.JFreeChart chartohlc = chartpanelohlc.getChart();
            //org.jfree.chart.plot.XYPlot plotohlc = (org.jfree.chart.plot.XYPlot)chartohlc.getPlot();
            //org.jfree.data.Range rangexohlc = plotohlc.getDomainAxis().getRange();
            
            org.jfree.chart.ChartPanel chartpanelsecundario = (org.jfree.chart.ChartPanel)jPanelSecondaryChartPanel.getComponent(0);
            org.jfree.chart.JFreeChart chartsecundario = chartpanelsecundario.getChart();
            org.jfree.chart.plot.XYPlot plotsecundario = (org.jfree.chart.plot.XYPlot)chartsecundario.getPlot();
            org.jfree.data.Range rangexsecundario = plotsecundario.getDomainAxis().getRange();
            
            //plotsecundario.getDomainAxis().setRange(plotohlc.getDomainAxis().getRange());
            
            if (mcg.rangex != rangexsecundario)
            {
               plotsecundario.getDomainAxis().setRange(mcg.rangex); 
            }
        }
  
    }
    // </editor-fold>


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jPanelFerramentasInfo = new javax.swing.JPanel();
        jButtonAtivarSelecao = new javax.swing.JButton();
        jButtonAtivarReta = new javax.swing.JButton();
        jButtonAtivarFibonacci = new javax.swing.JButton();
        jButtonAtivarRegua = new javax.swing.JButton();
        jButtonAtivarTexto = new javax.swing.JButton();
        jLabelFerramentas = new javax.swing.JLabel();
        jPanelPrincipal = new javax.swing.JPanel();
        jButtonEscolherSimbolo = new javax.swing.JButton();
        jLabelNomeSimbolo = new javax.swing.JLabel();
        jTextFieldNomeSimbolo = new javax.swing.JTextField();
        jLabelPeriodoSimbolo = new javax.swing.JLabel();
        jComboBoxPeriodoSimbolo = new javax.swing.JComboBox<>();
        jButtonCarregarConfiguracao = new javax.swing.JButton();
        jButtonSalvarConfiguracao = new javax.swing.JButton();
        jButtonAtualizarDadosGrafico = new javax.swing.JButton();
        jLabelLinearSwitch = new javax.swing.JLabel();
        jLabelLogaritmicaSwitch = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelIndicadores = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanelAnotacoes = new javax.swing.JPanel();
        jLabelPrincipal = new javax.swing.JLabel();
        jLabelIndicadores = new javax.swing.JLabel();
        jButtonAdicionarIndicador = new javax.swing.JButton();
        jLabelAnotacoes = new javax.swing.JLabel();
        jLabelInfo = new javax.swing.JLabel();
        jPanelChartHolders = new javax.swing.JPanel();
        jSplitPaneChartpanels = new javax.swing.JSplitPane();
        jPanelOHLCChartpanel = new javax.swing.JPanel();
        jPanelSecondaryChartPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(55, 55, 55));

        jPanelFerramentasInfo.setBackground(new java.awt.Color(120, 120, 120));

        jButtonAtivarSelecao.setForeground(new java.awt.Color(255, 0, 0));
        jButtonAtivarSelecao.setText("Select");
        jButtonAtivarSelecao.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAtivarSelecaoActionPerformed(evt);
            }
        });

        jButtonAtivarReta.setText("Line");
        jButtonAtivarReta.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAtivarRetaActionPerformed(evt);
            }
        });

        jButtonAtivarFibonacci.setText("Fibonacci");
        jButtonAtivarFibonacci.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAtivarFibonacciActionPerformed(evt);
            }
        });

        jButtonAtivarRegua.setText("Ruler");
        jButtonAtivarRegua.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAtivarReguaActionPerformed(evt);
            }
        });

        jButtonAtivarTexto.setText("Text");
        jButtonAtivarTexto.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAtivarTextoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelFerramentasInfoLayout = new javax.swing.GroupLayout(jPanelFerramentasInfo);
        jPanelFerramentasInfo.setLayout(jPanelFerramentasInfoLayout);
        jPanelFerramentasInfoLayout.setHorizontalGroup(
            jPanelFerramentasInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFerramentasInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonAtivarSelecao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAtivarTexto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAtivarReta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAtivarRegua)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAtivarFibonacci)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelFerramentasInfoLayout.setVerticalGroup(
            jPanelFerramentasInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFerramentasInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFerramentasInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAtivarSelecao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonAtivarReta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonAtivarFibonacci, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonAtivarRegua, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonAtivarTexto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jLabelFerramentas.setForeground(new java.awt.Color(255, 255, 255));
        jLabelFerramentas.setText("Tools");

        jPanelPrincipal.setBackground(new java.awt.Color(120, 120, 120));

        jButtonEscolherSimbolo.setText("Search");
        jButtonEscolherSimbolo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonEscolherSimboloActionPerformed(evt);
            }
        });

        jLabelNomeSimbolo.setForeground(new java.awt.Color(255, 255, 255));
        jLabelNomeSimbolo.setText("Symbol:");

        jTextFieldNomeSimbolo.setBackground(new java.awt.Color(125, 125, 125));
        jTextFieldNomeSimbolo.setForeground(new java.awt.Color(255, 255, 255));

        jLabelPeriodoSimbolo.setForeground(new java.awt.Color(255, 255, 255));
        jLabelPeriodoSimbolo.setText("Period:");

        jComboBoxPeriodoSimbolo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1 Day", "1 Month", "3 Months", "6 Months", "Year-to-date", "1 Year", "2 Years", "5 Years" }));

        jButtonCarregarConfiguracao.setText("Load Asset");
        jButtonCarregarConfiguracao.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonCarregarConfiguracaoActionPerformed(evt);
            }
        });

        jButtonSalvarConfiguracao.setText("Save Asset");
        jButtonSalvarConfiguracao.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonSalvarConfiguracaoActionPerformed(evt);
            }
        });

        jButtonAtualizarDadosGrafico.setText("Update");
        jButtonAtualizarDadosGrafico.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAtualizarDadosGraficoActionPerformed(evt);
            }
        });

        jLabelLinearSwitch.setForeground(new java.awt.Color(0, 0, 255));
        jLabelLinearSwitch.setText("Linear");
        jLabelLinearSwitch.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jLabelLinearSwitchMouseClicked(evt);
            }
        });

        jLabelLogaritmicaSwitch.setForeground(new java.awt.Color(255, 255, 255));
        jLabelLogaritmicaSwitch.setText("Logarithmic");
        jLabelLogaritmicaSwitch.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jLabelLogaritmicaSwitchMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanelPrincipalLayout = new javax.swing.GroupLayout(jPanelPrincipal);
        jPanelPrincipal.setLayout(jPanelPrincipalLayout);
        jPanelPrincipalLayout.setHorizontalGroup(
            jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPrincipalLayout.createSequentialGroup()
                        .addComponent(jLabelNomeSimbolo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldNomeSimbolo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonEscolherSimbolo))
                    .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                        .addComponent(jLabelPeriodoSimbolo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxPeriodoSimbolo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabelLinearSwitch)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelLogaritmicaSwitch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonAtualizarDadosGrafico, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                        .addComponent(jButtonCarregarConfiguracao, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSalvarConfiguracao)))
                .addContainerGap())
        );
        jPanelPrincipalLayout.setVerticalGroup(
            jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonEscolherSimbolo)
                    .addComponent(jLabelNomeSimbolo)
                    .addComponent(jTextFieldNomeSimbolo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxPeriodoSimbolo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPeriodoSimbolo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAtualizarDadosGrafico)
                    .addComponent(jLabelLinearSwitch)
                    .addComponent(jLabelLogaritmicaSwitch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCarregarConfiguracao)
                    .addComponent(jButtonSalvarConfiguracao))
                .addContainerGap())
        );

        jPanelIndicadores.setBackground(new java.awt.Color(120, 120, 120));

        javax.swing.GroupLayout jPanelIndicadoresLayout = new javax.swing.GroupLayout(jPanelIndicadores);
        jPanelIndicadores.setLayout(jPanelIndicadoresLayout);
        jPanelIndicadoresLayout.setHorizontalGroup(
            jPanelIndicadoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 376, Short.MAX_VALUE)
        );
        jPanelIndicadoresLayout.setVerticalGroup(
            jPanelIndicadoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 196, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanelIndicadores);

        jPanelAnotacoes.setBackground(new java.awt.Color(120, 120, 120));

        javax.swing.GroupLayout jPanelAnotacoesLayout = new javax.swing.GroupLayout(jPanelAnotacoes);
        jPanelAnotacoes.setLayout(jPanelAnotacoesLayout);
        jPanelAnotacoesLayout.setHorizontalGroup(
            jPanelAnotacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 507, Short.MAX_VALUE)
        );
        jPanelAnotacoesLayout.setVerticalGroup(
            jPanelAnotacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 196, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(jPanelAnotacoes);

        jLabelPrincipal.setForeground(new java.awt.Color(255, 255, 255));
        jLabelPrincipal.setText("Main");

        jLabelIndicadores.setForeground(new java.awt.Color(255, 255, 255));
        jLabelIndicadores.setText("Indicators");

        jButtonAdicionarIndicador.setText("Add");
        jButtonAdicionarIndicador.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAdicionarIndicadorActionPerformed(evt);
            }
        });

        jLabelAnotacoes.setForeground(new java.awt.Color(255, 255, 255));
        jLabelAnotacoes.setText("Annotations");

        jLabelInfo.setForeground(new java.awt.Color(255, 255, 255));
        jLabelInfo.setText("-");

        jSplitPaneChartpanels.setBackground(new java.awt.Color(55, 55, 55));
        jSplitPaneChartpanels.setDividerLocation(500);
        jSplitPaneChartpanels.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPaneChartpanels.addComponentListener(new java.awt.event.ComponentAdapter()
        {
            public void componentResized(java.awt.event.ComponentEvent evt)
            {
                jSplitPaneChartpanelsComponentResized(evt);
            }
        });

        jPanelOHLCChartpanel.setBackground(new java.awt.Color(0, 0, 0));
        jPanelOHLCChartpanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        javax.swing.GroupLayout jPanelOHLCChartpanelLayout = new javax.swing.GroupLayout(jPanelOHLCChartpanel);
        jPanelOHLCChartpanel.setLayout(jPanelOHLCChartpanelLayout);
        jPanelOHLCChartpanelLayout.setHorizontalGroup(
            jPanelOHLCChartpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelOHLCChartpanelLayout.setVerticalGroup(
            jPanelOHLCChartpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jSplitPaneChartpanels.setTopComponent(jPanelOHLCChartpanel);

        jPanelSecondaryChartPanel.setBackground(new java.awt.Color(45, 45, 45));

        javax.swing.GroupLayout jPanelSecondaryChartPanelLayout = new javax.swing.GroupLayout(jPanelSecondaryChartPanel);
        jPanelSecondaryChartPanel.setLayout(jPanelSecondaryChartPanelLayout);
        jPanelSecondaryChartPanelLayout.setHorizontalGroup(
            jPanelSecondaryChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelSecondaryChartPanelLayout.setVerticalGroup(
            jPanelSecondaryChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jSplitPaneChartpanels.setBottomComponent(jPanelSecondaryChartPanel);

        javax.swing.GroupLayout jPanelChartHoldersLayout = new javax.swing.GroupLayout(jPanelChartHolders);
        jPanelChartHolders.setLayout(jPanelChartHoldersLayout);
        jPanelChartHoldersLayout.setHorizontalGroup(
            jPanelChartHoldersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPaneChartpanels)
        );
        jPanelChartHoldersLayout.setVerticalGroup(
            jPanelChartHoldersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPaneChartpanels, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelFerramentasInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelPrincipal))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelIndicadores)
                                .addGap(18, 18, 18)
                                .addComponent(jButtonAdicionarIndicador)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelAnotacoes)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelFerramentas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelInfo))
                    .addComponent(jPanelChartHolders, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelFerramentas)
                    .addComponent(jLabelInfo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelFerramentasInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelChartHolders, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelAnotacoes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabelIndicadores)
                            .addComponent(jButtonAdicionarIndicador))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelPrincipal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonAtivarRetaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAtivarRetaActionPerformed
    {//GEN-HEADEREND:event_jButtonAtivarRetaActionPerformed
        mcg.trocarferramentaparareta();
        resetarcorbotoesferramentas();
        jButtonAtivarReta.setForeground(Color.red);
    }//GEN-LAST:event_jButtonAtivarRetaActionPerformed

    private void jButtonAtivarSelecaoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAtivarSelecaoActionPerformed
    {//GEN-HEADEREND:event_jButtonAtivarSelecaoActionPerformed
        mcg.trocarferramentaparaselecao();
        resetarcorbotoesferramentas();
        jButtonAtivarSelecao.setForeground(Color.red);
    }//GEN-LAST:event_jButtonAtivarSelecaoActionPerformed

    private void jButtonAdicionarIndicadorActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAdicionarIndicadorActionPerformed
    {//GEN-HEADEREND:event_jButtonAdicionarIndicadorActionPerformed
        frames.analisadorasset.grafico.adicionarindicador mfai = new frames.analisadorasset.grafico.adicionarindicador(this);
        mfai.show();
    }//GEN-LAST:event_jButtonAdicionarIndicadorActionPerformed

    private void jButtonAtualizarDadosGraficoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAtualizarDadosGraficoActionPerformed
    {//GEN-HEADEREND:event_jButtonAtualizarDadosGraficoActionPerformed
        if (poderecarregar == false)
        {
           criargraficoohlc();
           poderecarregar = true;
        }
        else if (poderecarregar == true)
        {
            recarregardadosasset();
        }
    }//GEN-LAST:event_jButtonAtualizarDadosGraficoActionPerformed

    private void jButtonEscolherSimboloActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonEscolherSimboloActionPerformed
    {//GEN-HEADEREND:event_jButtonEscolherSimboloActionPerformed
        frames.analisadorasset.grafico.adicionarsimbolo as = new frames.analisadorasset.grafico.adicionarsimbolo(this);
        as.show();
    }//GEN-LAST:event_jButtonEscolherSimboloActionPerformed

    private void jButtonSalvarConfiguracaoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonSalvarConfiguracaoActionPerformed
    {//GEN-HEADEREND:event_jButtonSalvarConfiguracaoActionPerformed
        salvarconfiguracaoasset();
    }//GEN-LAST:event_jButtonSalvarConfiguracaoActionPerformed

    private void jButtonCarregarConfiguracaoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonCarregarConfiguracaoActionPerformed
    {//GEN-HEADEREND:event_jButtonCarregarConfiguracaoActionPerformed
        carregarconfiguracaoasset();
    }//GEN-LAST:event_jButtonCarregarConfiguracaoActionPerformed

    private void jLabelLinearSwitchMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jLabelLinearSwitchMouseClicked
    {//GEN-HEADEREND:event_jLabelLinearSwitchMouseClicked
        alternartipoescala("linear");
    }//GEN-LAST:event_jLabelLinearSwitchMouseClicked

    private void jLabelLogaritmicaSwitchMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jLabelLogaritmicaSwitchMouseClicked
    {//GEN-HEADEREND:event_jLabelLogaritmicaSwitchMouseClicked
        alternartipoescala("logaritmica");
    }//GEN-LAST:event_jLabelLogaritmicaSwitchMouseClicked

    private void jButtonAtivarFibonacciActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAtivarFibonacciActionPerformed
    {//GEN-HEADEREND:event_jButtonAtivarFibonacciActionPerformed
        mcg.trocarferramentaparafibonacci();
        resetarcorbotoesferramentas();
        jButtonAtivarFibonacci.setForeground(Color.red);
    }//GEN-LAST:event_jButtonAtivarFibonacciActionPerformed

    private void jButtonAtivarReguaActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAtivarReguaActionPerformed
    {//GEN-HEADEREND:event_jButtonAtivarReguaActionPerformed
        mcg.trocarferramentapararegua();
        resetarcorbotoesferramentas();
        jButtonAtivarRegua.setForeground(Color.red);
    }//GEN-LAST:event_jButtonAtivarReguaActionPerformed

    private void jButtonAtivarTextoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAtivarTextoActionPerformed
    {//GEN-HEADEREND:event_jButtonAtivarTextoActionPerformed
        mcg.trocarferramentaparatexto();
        resetarcorbotoesferramentas();
        jButtonAtivarTexto.setForeground(Color.red);
    }//GEN-LAST:event_jButtonAtivarTextoActionPerformed

    private void jSplitPaneChartpanelsComponentResized(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_jSplitPaneChartpanelsComponentResized
    {//GEN-HEADEREND:event_jSplitPaneChartpanelsComponentResized
        if (jPanelSecondaryChartPanel.getComponentCount() == 0)
        {
            jSplitPaneChartpanels.setDividerLocation(500);
        }
    }//GEN-LAST:event_jSplitPaneChartpanelsComponentResized


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdicionarIndicador;
    private javax.swing.JButton jButtonAtivarFibonacci;
    private javax.swing.JButton jButtonAtivarRegua;
    private javax.swing.JButton jButtonAtivarReta;
    private javax.swing.JButton jButtonAtivarSelecao;
    private javax.swing.JButton jButtonAtivarTexto;
    private javax.swing.JButton jButtonAtualizarDadosGrafico;
    private javax.swing.JButton jButtonCarregarConfiguracao;
    private javax.swing.JButton jButtonEscolherSimbolo;
    private javax.swing.JButton jButtonSalvarConfiguracao;
    private javax.swing.JComboBox<String> jComboBoxPeriodoSimbolo;
    private javax.swing.JLabel jLabelAnotacoes;
    private javax.swing.JLabel jLabelFerramentas;
    private javax.swing.JLabel jLabelIndicadores;
    private javax.swing.JLabel jLabelInfo;
    private javax.swing.JLabel jLabelLinearSwitch;
    private javax.swing.JLabel jLabelLogaritmicaSwitch;
    private javax.swing.JLabel jLabelNomeSimbolo;
    private javax.swing.JLabel jLabelPeriodoSimbolo;
    private javax.swing.JLabel jLabelPrincipal;
    private javax.swing.JPanel jPanelAnotacoes;
    private javax.swing.JPanel jPanelChartHolders;
    private javax.swing.JPanel jPanelFerramentasInfo;
    private javax.swing.JPanel jPanelIndicadores;
    private javax.swing.JPanel jPanelOHLCChartpanel;
    private javax.swing.JPanel jPanelPrincipal;
    private javax.swing.JPanel jPanelSecondaryChartPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPaneChartpanels;
    private javax.swing.JTextField jTextFieldNomeSimbolo;
    // End of variables declaration//GEN-END:variables
}
