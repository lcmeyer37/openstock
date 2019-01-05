/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mierpanels;

import java.awt.Color;
import mierclasses.mcfuncoeshelper;

/**
 *
 * @author lucasmeyer
 */
public class mpsubmodulografico extends javax.swing.JPanel
{
    //jpanel que vai conter um grafico para analise
    
    public mierpanels.mpitemgrafico mtgraficopai; //item grafico associado a este submodulo
    
    public mierclasses.mcchartgenerator mcg; //classe utilizada para desenhar graficos
    
    public mierclasses.mcavsearchresultcandle mbmsimboloatual; //variavel que contem o simbolo atual em uso
    
    
    // <editor-fold defaultstate="collapsed" desc="Construtores Submodulo Grafico">
    
    //construtor novo submodulo grafico
    public mpsubmodulografico(mierpanels.mpitemgrafico mtgpai)
    {
        initComponents();
        
        mtgraficopai = mtgpai;
        
        inicializarsubmodulografico();
    }
    
    //inicializacao de novo submodulo grafico
    void inicializarsubmodulografico()
    {
        //popular objeto utilizado para desenho de graficos
        mcg = new mierclasses.mcchartgenerator();
        
        jPanelAnotacoes.setLayout(new java.awt.GridLayout(20,1));
        jPanelIndicadores.setLayout(new java.awt.GridLayout(20,1));
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Main Section">
    
    public void adicionarsimboloaotextboxsubmodulo(mierclasses.mcavsearchresultcandle simboloadicionar)
    {
        //funcao para adicionar o codigo do simbolo no textbox de simbolo
        jTextFieldNomeSimbolo.setText(simboloadicionar.symbolstr);
        mbmsimboloatual = simboloadicionar;
    }
    
    //funcao responsavel por carregar o grafico com o simbolo e periodo desejado
    public void recarregargraficosimbolo()
    {
        //receber informacoes de candles de acordo com os paremetros principais
        String simboloescolhido = jTextFieldNomeSimbolo.getText();
        String periodoescolhido = jComboBoxPeriodoSimbolo.getSelectedItem().toString();
        
        java.util.List<mierclasses.mccandle> candles = null;
        if (periodoescolhido.equals("1 minute"))
            candles = mtgraficopai.tprincipalpai.mav.receberstockcandlesintraday(simboloescolhido,"1min","");
        else if (periodoescolhido.equals("5 minutes"))
            candles = mtgraficopai.tprincipalpai.mav.receberstockcandlesintraday(simboloescolhido,"5min","");
        else if (periodoescolhido.equals("15 minutes"))
            candles = mtgraficopai.tprincipalpai.mav.receberstockcandlesintraday(simboloescolhido,"15min","");
        else if (periodoescolhido.equals("30 minutes"))
            candles = mtgraficopai.tprincipalpai.mav.receberstockcandlesintraday(simboloescolhido,"30min","");
        else if (periodoescolhido.equals("60 minutes"))
            candles = mtgraficopai.tprincipalpai.mav.receberstockcandlesintraday(simboloescolhido,"60min","");
        else if (periodoescolhido.equals("Daily"))
            candles = mtgraficopai.tprincipalpai.mav.receberstockcandlesdaily(simboloescolhido, "");
        else if (periodoescolhido.equals("Weekly"))
            candles = mtgraficopai.tprincipalpai.mav.receberstockcandlesweekly(simboloescolhido, "");
        else if (periodoescolhido.equals("Monthly"))
            candles = mtgraficopai.tprincipalpai.mav.receberstockcandlesmonthly(simboloescolhido, "");
        
        //recriar grafico OHLC resetando anotacoes e indicadores atuais
        mcg.recriarohlc(candles, mbmsimboloatual);
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
        jPanelChartpanelholder.removeAll();
        jPanelChartpanelholder.setLayout(new java.awt.BorderLayout());
        jPanelChartpanelholder.add(chartpanel);
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
            mierpanels.mpitemindicador miia = (mierpanels.mpitemindicador)jPanelIndicadores.getComponent(i);
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
            mierpanels.mpitemanotacao miaa = (mierpanels.mpitemanotacao)jPanelAnotacoes.getComponent(i);
            
            String tipoanotacao = miaa.tipoanotacao;
            String anotacaoserializada = "";
            
            if (tipoanotacao.equals("line"))
            {
                org.jfree.chart.annotations.XYLineAnnotation lineserialize = 
                        (org.jfree.chart.annotations.XYLineAnnotation) miaa.annotation;
                
                //making serialization of the annotations
                //https://www.tutorialspoint.com/java/java_serialization.htm
                //https://stackoverflow.com/questions/134492/how-to-serialize-an-object-into-a-string
                try
                {
                    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                    java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
                    oos.writeObject(lineserialize);
                    oos.close();
                    anotacaoserializada = java.util.Base64.getEncoder().encodeToString(baos.toByteArray()); 
                }
                catch (java.io.IOException ex)
                {
                    //necessario
                }
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
        
        
        String xmlSalvar =
                "<?xml version=\"1.0\"?>" +
                    "<OpenstockAssetSave>" +
                        
                        "<MainInfo>" +
                            "<Name>" + mtgraficopai.jLabelNomeItemGrafico.getText() + "</Name>" +
                            "<ID>" + mtgraficopai.id + "</ID>" +
                            "<Symbol>" + jTextFieldNomeSimbolo.getText() + "</Symbol>" +
                            "<Period>" + jComboBoxPeriodoSimbolo.getSelectedItem().toString() + "</Period>" +
                        "</MainInfo>" +
                
                        "<IndicatorsInfo>" +
                            subxmlIndicadores +
                        "</IndicatorsInfo>" +
                
                        "<AnnotationsInfo>" +
                            subxmlAnotacoes +
                        "</AnnotationsInfo>" +
                
                    "</<OpenstockAssetSave>";
        
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
        }
        catch (Exception ex)
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("A problem occurred when saving. Exception: " + ex.getMessage());
        }

    }
    
    public void carregarconfiguracaoasset()
    {
        //funcao para salvar as configuracoes de
        //simbolo, periodo
        //indicadores
        //anotacoes
        
        //IMPLEMENTAR!
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Indicators Section">
        
    public void adicionarIndicador(String idbc, String paramsbc)
    {
        //funcao para adicionar novo item indicador a este submodulo grafico
        
        //public mpitemindicador(mierpanels.mpsubmodulografico mpsmg, String idbearcode, String parametrosbearcode)
        mierpanels.mpitemindicador novoindicador = new mierpanels.mpitemindicador(this, idbc, paramsbc);
        String statusrunindicador = novoindicador.rodarscriptindicadoredesenhar();
        if (statusrunindicador.equals("ok"))
        {
            mcg.adicionarplotohlc_indicadorid(novoindicador.id);
            jPanelIndicadores.add(novoindicador);
            this.validate();
            this.repaint(); 
        }
        else
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("Algum problema ocorreu e o indicador não pôde ser utilizado:\n\n" + statusrunindicador);
        }

    }
    
    public void removerIndicador(mierpanels.mpitemindicador mpiiremover)
    {
        mcg.removerplotohlc_indicador(mpiiremover.id);
        jPanelIndicadores.remove(mpiiremover);
        this.validate();
        this.repaint();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Annotations Section">
    
    void atualizarlistaannotationsgrafico()
    {
        java.util.List<Object> lan = mcg.retornarlistaanotacoesatuais();
        int tamanhoanotacoesgraficas = lan.size();
        
        int tamanhoidsatual = mcg.idanotacoesatual.size();
        
        if (tamanhoanotacoesgraficas > tamanhoidsatual)
        {
            //considerando que temos mais anotacoes graficas que ids na lista de ids do chartgenerator,
            //conclui-se que uma nova anotacao acabou de ser criada graficamente, 
            //entao um novo id deve ser adicionado a lista de anotacoes
            //e um novo item de anotacao deve ser criado
            
            //eh necessario saber qual a ferramenta atual em uso para saber qual tipo
            //de anotacao esta sendo adicionada
            if (mcg.ferramentaatualgrafico.equals("reta"))
            {
                mierpanels.mpitemanotacao novompia = new mierpanels.mpitemanotacao(this,"line",lan.get(tamanhoanotacoesgraficas-1));
                mcg.adicionarplotohlc_annotationid(novompia.id);
                jPanelAnotacoes.add(novompia);
            }
        }
        
        this.validate();
        this.repaint();
    }
    
    public void removerAnotacao(mierpanels.mpitemanotacao mpiaremover)
    {

        mcg.removerplotohlc_annotation(mpiaremover.id);
        jPanelAnotacoes.remove(mpiaremover);
        this.validate();
        this.repaint();
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Tools and OHLC Section">
    
    // <editor-fold defaultstate="collapsed" desc="Funcoes de interpretacao de mouse events do jfreechart">
    void interpretarmouseclickchart(org.jfree.chart.ChartMouseEvent e)
    {
        atualizarlistaannotationsgrafico();
    }
    
    void interpretarmousemovechart(org.jfree.chart.ChartMouseEvent e)
    {       
        atualizarinformacoesposicaoatualgrafico();
    }
    //</editor-fold>
    
    void resetarcorbotoesferramentas()
    {
        jButtonAtivarSelecao.setForeground(Color.black);
        jButtonAtivarReta.setForeground(Color.black);
    }
        
    void atualizarinformacoesposicaoatualgrafico()
    {
             String valoryatualohlc = String.format( "%.4f", mcg.valormouseatualgraficoy);
        java.util.Date dataatualohlc = new java.util.Date((long)mcg.valormouseatualgraficox);
        jLabelInfo.setText("Price: " + valoryatualohlc + " Date: " + dataatualohlc.toString());   
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

        jPanelChartpanelholder = new javax.swing.JPanel();
        jPanelFerramentasInfo = new javax.swing.JPanel();
        jButtonAtivarSelecao = new javax.swing.JButton();
        jButtonAtivarReta = new javax.swing.JButton();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanelIndicadores = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanelAnotacoes = new javax.swing.JPanel();
        jLabelPrincipal = new javax.swing.JLabel();
        jLabelIndicadores = new javax.swing.JLabel();
        jButtonAdicionarIndicador = new javax.swing.JButton();
        jLabelAnotacoes = new javax.swing.JLabel();
        jLabelInfo = new javax.swing.JLabel();

        setBackground(new java.awt.Color(55, 55, 55));

        jPanelChartpanelholder.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanelChartpanelholderLayout = new javax.swing.GroupLayout(jPanelChartpanelholder);
        jPanelChartpanelholder.setLayout(jPanelChartpanelholderLayout);
        jPanelChartpanelholderLayout.setHorizontalGroup(
            jPanelChartpanelholderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelChartpanelholderLayout.setVerticalGroup(
            jPanelChartpanelholderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 377, Short.MAX_VALUE)
        );

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

        javax.swing.GroupLayout jPanelFerramentasInfoLayout = new javax.swing.GroupLayout(jPanelFerramentasInfo);
        jPanelFerramentasInfo.setLayout(jPanelFerramentasInfoLayout);
        jPanelFerramentasInfoLayout.setHorizontalGroup(
            jPanelFerramentasInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelFerramentasInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonAtivarSelecao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonAtivarReta)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelFerramentasInfoLayout.setVerticalGroup(
            jPanelFerramentasInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelFerramentasInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelFerramentasInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAtivarSelecao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonAtivarReta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jTextFieldNomeSimbolo.setEditable(false);
        jTextFieldNomeSimbolo.setBackground(new java.awt.Color(125, 125, 125));
        jTextFieldNomeSimbolo.setForeground(new java.awt.Color(255, 255, 255));

        jLabelPeriodoSimbolo.setForeground(new java.awt.Color(255, 255, 255));
        jLabelPeriodoSimbolo.setText("Period:");

        jComboBoxPeriodoSimbolo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1 minute", "5 minutes", "15 minutes", "30 minutes", "60 minutes", "Daily", "Weekly", "Monthly" }));

        jButtonCarregarConfiguracao.setText("Load Asset");

        jButtonSalvarConfiguracao.setText("Save Asset");
        jButtonSalvarConfiguracao.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonSalvarConfiguracaoActionPerformed(evt);
            }
        });

        jButtonAtualizarDadosGrafico.setText("Update Symbol Data");
        jButtonAtualizarDadosGrafico.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonAtualizarDadosGraficoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelPrincipalLayout = new javax.swing.GroupLayout(jPanelPrincipal);
        jPanelPrincipal.setLayout(jPanelPrincipalLayout);
        jPanelPrincipalLayout.setHorizontalGroup(
            jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonAtualizarDadosGrafico, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                        .addComponent(jButtonCarregarConfiguracao)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonSalvarConfiguracao)
                        .addGap(0, 0, Short.MAX_VALUE))))
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
                .addComponent(jButtonAtualizarDadosGrafico)
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelChartpanelholder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonAdicionarIndicador)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelAnotacoes)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelFerramentas)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabelInfo)))
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
                .addComponent(jPanelChartpanelholder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelIndicadores)
                    .addComponent(jButtonAdicionarIndicador)
                    .addComponent(jLabelPrincipal)
                    .addComponent(jLabelAnotacoes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        mierframes.mfadicionarindicador mfai = new mierframes.mfadicionarindicador(this);
        mfai.show();
    }//GEN-LAST:event_jButtonAdicionarIndicadorActionPerformed

    private void jButtonAtualizarDadosGraficoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonAtualizarDadosGraficoActionPerformed
    {//GEN-HEADEREND:event_jButtonAtualizarDadosGraficoActionPerformed
        recarregargraficosimbolo();
    }//GEN-LAST:event_jButtonAtualizarDadosGraficoActionPerformed

    private void jButtonEscolherSimboloActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonEscolherSimboloActionPerformed
    {//GEN-HEADEREND:event_jButtonEscolherSimboloActionPerformed
        mierframes.mfadicionarsimbolo as = new mierframes.mfadicionarsimbolo(this);
        as.show();
    }//GEN-LAST:event_jButtonEscolherSimboloActionPerformed

    private void jButtonSalvarConfiguracaoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonSalvarConfiguracaoActionPerformed
    {//GEN-HEADEREND:event_jButtonSalvarConfiguracaoActionPerformed
        salvarconfiguracaoasset();
    }//GEN-LAST:event_jButtonSalvarConfiguracaoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAdicionarIndicador;
    private javax.swing.JButton jButtonAtivarReta;
    private javax.swing.JButton jButtonAtivarSelecao;
    private javax.swing.JButton jButtonAtualizarDadosGrafico;
    private javax.swing.JButton jButtonCarregarConfiguracao;
    private javax.swing.JButton jButtonEscolherSimbolo;
    private javax.swing.JButton jButtonSalvarConfiguracao;
    private javax.swing.JComboBox<String> jComboBoxPeriodoSimbolo;
    private javax.swing.JLabel jLabelAnotacoes;
    private javax.swing.JLabel jLabelFerramentas;
    private javax.swing.JLabel jLabelIndicadores;
    private javax.swing.JLabel jLabelInfo;
    private javax.swing.JLabel jLabelNomeSimbolo;
    private javax.swing.JLabel jLabelPeriodoSimbolo;
    private javax.swing.JLabel jLabelPrincipal;
    private javax.swing.JPanel jPanelAnotacoes;
    private javax.swing.JPanel jPanelChartpanelholder;
    private javax.swing.JPanel jPanelFerramentasInfo;
    private javax.swing.JPanel jPanelIndicadores;
    private javax.swing.JPanel jPanelPrincipal;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextFieldNomeSimbolo;
    // End of variables declaration//GEN-END:variables
}
