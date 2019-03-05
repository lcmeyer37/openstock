/*
 * Copyright (C) 2019 Lucas Meyer
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
package outrasopcoes.editorbearcodetraderbot;

import java.awt.BasicStroke;
import java.awt.Color;
import org.jfree.ui.TextAnchor;

/**
 *
 * @author meyerlu
 */


public class frame_resultadosbearcodetraderbot extends javax.swing.JFrame
{
    static String dadoscsvresultado;

    /**
     * Creates new form frame_resultadosbearcodetraderbot
     */
    public frame_resultadosbearcodetraderbot(String dadoscsv)
    {
        initComponents();
        
        dadoscsvresultado = dadoscsv;
        inicializar();
    }
    
    void inicializar()
    {
        //criar tabela para mostrar resultados
        criartabelacomcsv();
        
        //criar grafico para mostrar closes e decisoes de compra e venda
        criargraficocomcsv("Close Values and Bot Decisions",2,1,5,10);
        jPanelChartResults.removeAll();
        jPanelChartResults.setLayout(new java.awt.BorderLayout());
        jPanelChartResults.add(chartpanelseparado);
        //tirar cursor do chart
        java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
        byte bogus[] = { (byte) 0 };
        java.awt.Cursor blankCursor = tk.createCustomCursor( tk.createImage( bogus ), new java.awt.Point(0, 0), "" );
        jPanelChartResults.setCursor(blankCursor);
        
        this.validate();
    }
    
    org.jfree.chart.ChartPanel chartpanelseparado = null;
    void criargraficocomcsv(String tituloscript, int indexcolunay, int indexcolunax, int indexcolunabotdecision, int indexautotraderlog)
    {
        String[] linhascsv = dadoscsvresultado.split("\n");
        Integer numerolinhas = linhascsv.length; //numero total de linhas
        
        //<editor-fold defaultstate="collapsed" desc="popular yvalues_double, xvalues_date e botdecisions_string">
        java.util.List<Double> yv_list = new java.util.ArrayList<Double>();
        java.util.List<java.util.Date> xv_list = new java.util.ArrayList<java.util.Date>();
        java.util.List<String> bt_list = new java.util.ArrayList<String>();
        java.util.List<String> log_list = new java.util.ArrayList<String>();
        for (int i = 1; i < numerolinhas; i++)
        {
            String[] dadoslinha_atual = linhascsv[i].split(";");
            double yvalue_double_atual = Double.valueOf(dadoslinha_atual[indexcolunay]);
            //mierclasses.mcfuncoeshelper.mostrarmensagem(String.valueOf(yvalue_double_atual));
            yv_list.add(yvalue_double_atual);
            
            String xvalue_string_atual = String.valueOf(dadoslinha_atual[indexcolunax]);
            String[] dataitems = xvalue_string_atual.split("-"); //YYYY-MM-DD-HH-mm-ss
            java.util.Date xvalue_date_atual =
                new java.util.Date
                (
                    Integer.valueOf(dataitems[0]) - 1900, 
                    Integer.valueOf(dataitems[1]) - 1, 
                    Integer.valueOf(dataitems[2]), 
                    Integer.valueOf(dataitems[3]), 
                    Integer.valueOf(dataitems[4]), 
                    Integer.valueOf(dataitems[5])
                );
            //mierclasses.mcfuncoeshelper.mostrarmensagem(String.valueOf(xvalue_date_atual));
            xv_list.add(xvalue_date_atual);
            
            String botdecision_string_atual = String.valueOf(dadoslinha_atual[indexcolunabotdecision]);
            //mierclasses.mcfuncoeshelper.mostrarmensagem(String.valueOf(botdecision_string_atual));
            bt_list.add(botdecision_string_atual);
            
            String autotraderlog_string_atual = String.valueOf(dadoslinha_atual[indexautotraderlog]);
            //mierclasses.mcfuncoeshelper.mostrarmensagem(String.valueOf(botdecision_string_atual));
            log_list.add(autotraderlog_string_atual);
        }
        
        double[] yvalues_double = new double[yv_list.size()];
        for (int i = 0; i < yvalues_double.length; i++)
        {
            yvalues_double[i] = yv_list.get(i);
        }
        java.util.Date[] xvalues_date = new java.util.Date[xv_list.size()];
        for (int i = 0; i < xvalues_date.length; i++)
        {
            xvalues_date[i] = xv_list.get(i);
        }
        String[] botdecisions_string = new String[bt_list.size()];
        for (int i = 0; i < botdecisions_string.length; i++)
        {
            botdecisions_string[i] = bt_list.get(i);
        }
        String[] autotraderlogs_string = new String[bt_list.size()];
        for (int i = 0; i < autotraderlogs_string.length; i++)
        {
            autotraderlogs_string[i] = log_list.get(i);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="criar timeseries com yvalues_double e xvalues_date">
        org.jfree.data.time.TimeSeries seriesadd = new org.jfree.data.time.TimeSeries(tituloscript);
        for (int i = 0; i < yvalues_double.length; i++)
        {

            org.jfree.data.time.Millisecond millisegundoatual
                    = new org.jfree.data.time.Millisecond(xvalues_date[i]);

            double valoratual = yvalues_double[i];

            seriesadd.add(millisegundoatual, valoratual);
        }

        //criar dataset e associar timeseries ao dataset
        org.jfree.data.time.TimeSeriesCollection datasettimeseries = new org.jfree.data.time.TimeSeriesCollection();
        datasettimeseries.addSeries(seriesadd);

        //criar renderer
        org.jfree.chart.renderer.xy.XYLineAndShapeRenderer renderer = new org.jfree.chart.renderer.xy.DefaultXYItemRenderer();
        renderer.setBaseShapesVisible(false);
        renderer.setSeriesStroke(0, new BasicStroke(0.75f));
        renderer.setSeriesPaint(0, Color.BLACK);

        //criar ranges
        org.jfree.chart.axis.DateAxis domainAxis = new org.jfree.chart.axis.DateAxis("");
        org.jfree.chart.axis.NumberAxis rangeAxis = new org.jfree.chart.axis.NumberAxis("");

        //criar xyplot e associar dataset e renderer ao xyplot
        org.jfree.chart.plot.XYPlot plot = new org.jfree.chart.plot.XYPlot(datasettimeseries,domainAxis,rangeAxis,renderer);
        plot.setDataset(datasettimeseries);
        plot.setRenderer(renderer);

        //criar chart
        org.jfree.chart.JFreeChart chart = new org.jfree.chart.JFreeChart(tituloscript.toUpperCase(), null,plot,false);

        //criar chartpanel
        chartpanelseparado = new org.jfree.chart.ChartPanel(chart);
        chartpanelseparado.addChartMouseListener(new org.jfree.chart.ChartMouseListener()
        {
            public void chartMouseClicked(org.jfree.chart.ChartMouseEvent e)
            {
            }

            public void chartMouseMoved(org.jfree.chart.ChartMouseEvent e)
            {
                //adicionando crosshair a este grafico
                executaranotacaofixa_crosshair_mmove(e);
            }
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="adicionar anotacoes de compra e venda com botdecisions_string">
        org.jfree.data.Range rangex = plot.getDomainAxis().getRange();
        org.jfree.data.Range rangey = plot.getRangeAxis().getRange();
        
        for (int i = 0; i < yvalues_double.length; i++)
        {
            double valorx_double = xvalues_date[i].getTime();
            double valory_double = yvalues_double[i];
            String decisaoatual = botdecisions_string[i];
            String logatual = autotraderlogs_string[i];
   
            //codigo ignorando print de transacoes erradas
            double lv_p1_x = valorx_double;
            double lv_p1_y = rangey.getUpperBound();
            double lv_p2_x = valorx_double;
            double lv_p2_y = rangey.getLowerBound();
            if (logatual.equals("ok"))
            { 
                if (decisaoatual.equals("buyall") || decisaoatual.equals("buyamount"))
                {
                    //mierclasses.mcfuncoeshelper.mostrarmensagem("ADICIONANDO BUY LINE");
                    org.jfree.chart.annotations.XYLineAnnotation xylv = new org.jfree.chart.annotations.XYLineAnnotation(lv_p1_x, lv_p1_y, lv_p2_x, lv_p2_y, new BasicStroke(0.75f), Color.GREEN);
                    plot.addAnnotation(xylv);
                }
                else if (decisaoatual.equals("sellall") || decisaoatual.equals("sellamount"))
                {
                    //mierclasses.mcfuncoeshelper.mostrarmensagem("ADICIONANDO SELL LINE");
                    org.jfree.chart.annotations.XYLineAnnotation xylv = new org.jfree.chart.annotations.XYLineAnnotation(lv_p1_x, lv_p1_y, lv_p2_x, lv_p2_y, new BasicStroke(0.75f), Color.RED);
                    plot.addAnnotation(xylv);
                }  
            }
            else
            {
                //org.jfree.chart.annotations.XYLineAnnotation xylv = new org.jfree.chart.annotations.XYLineAnnotation(lv_p1_x, lv_p1_y, lv_p2_x, lv_p2_y, new BasicStroke(0.1f), Color.BLUE);
                //plot.addAnnotation(xylv);
            }
           
        }
        //</editor-fold>
    }
    
    // <editor-fold defaultstate="collapsed" desc="Adicionando Crosshair a este grafico">
    java.util.List<org.jfree.chart.annotations.XYAnnotation> crosshair_preview;
    void executaranotacaofixa_crosshair_mmove(org.jfree.chart.ChartMouseEvent cmevent)
    {
        try
        {
            org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot) chartpanelseparado.getChart().getPlot();

            for (int i = 0; i < crosshair_preview.size(); i++)
            {
                plotatual.removeAnnotation((org.jfree.chart.annotations.XYAnnotation) crosshair_preview.get(i));
            }
        } 
        catch (Exception ex)
        {
        }

        crosshair_preview = adicionarplotohlc_anotacaofixacrosshair(cmevent);
    }
    
    public java.util.List<org.jfree.chart.annotations.XYAnnotation> adicionarplotohlc_anotacaofixacrosshair(org.jfree.chart.ChartMouseEvent cmevent)
    {

        //atualizar range e posicao atual do grafico
        java.awt.geom.Point2D p = chartpanelseparado.translateScreenToJava2D(cmevent.getTrigger().getPoint());
        java.awt.geom.Rectangle2D plotArea = chartpanelseparado.getChartRenderingInfo().getPlotInfo().getDataArea();
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) chartpanelseparado.getChart().getPlot(); // your plot
        double mcg_posmousex = plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());
        double mcg_posmousey = plot.getRangeAxis().java2DToValue(p.getY(), plotArea, plot.getRangeAxisEdge());
        org.jfree.data.Range mcg_rangex = plot.getDomainAxis().getRange();
        org.jfree.data.Range mcg_rangey = plot.getRangeAxis().getRange();

        //ponto central do crosshair
        double centrocrosshair_x = mcg_posmousex;
        double centrocrosshair_y = mcg_posmousey;
        
        //criando linha horizontal cinza
        double lh_p1_x = mcg_rangex.getLowerBound();
        double lh_p1_y = centrocrosshair_y;
        double lh_p2_x = mcg_rangex.getUpperBound();
        double lh_p2_y = centrocrosshair_y;
        org.jfree.chart.annotations.XYLineAnnotation xylh = new org.jfree.chart.annotations.XYLineAnnotation(lh_p1_x, lh_p1_y, lh_p2_x, lh_p2_y, new BasicStroke(0.4f), Color.BLUE);

        //criando linha vertical cinza
        double lv_p1_x = centrocrosshair_x;
        double lv_p1_y = mcg_rangey.getUpperBound();
        double lv_p2_x = centrocrosshair_x;
        double lv_p2_y = mcg_rangey.getLowerBound();
        org.jfree.chart.annotations.XYLineAnnotation xylv = new org.jfree.chart.annotations.XYLineAnnotation(lv_p1_x, lv_p1_y, lv_p2_x, lv_p2_y, new BasicStroke(0.4f), Color.BLUE);
        
        //texto preco
        String textopreco = String.format("%.4f", mcg_posmousey);
        org.jfree.chart.annotations.XYTextAnnotation xytextopreco = new org.jfree.chart.annotations.XYTextAnnotation(textopreco, lh_p1_x,centrocrosshair_y);
        xytextopreco.setTextAnchor(TextAnchor.TOP_LEFT);
        
        //texto data
        String datacrosshair = new java.util.Date((long) mcg_posmousex).toString();
        String textodata = datacrosshair;
        org.jfree.chart.annotations.XYTextAnnotation xytextodata = new org.jfree.chart.annotations.XYTextAnnotation(textodata, centrocrosshair_x, lv_p2_y);
        xytextodata.setTextAnchor(TextAnchor.BOTTOM_LEFT);
        
        plot.addAnnotation(xylh);
        plot.addAnnotation(xylv);
        plot.addAnnotation(xytextopreco);
        plot.addAnnotation(xytextodata);

        java.util.List<org.jfree.chart.annotations.XYAnnotation> subannotations = new java.util.ArrayList<>();
        subannotations.add(xylh);
        subannotations.add(xylv);
        subannotations.add(xytextopreco);
        subannotations.add(xytextodata);

        return subannotations;
    }

    // </editor-fold>
    
    void criartabelacomcsv()
    {
        String[] linhascsv = dadoscsvresultado.split("\n");
        String[] nomescolunas = linhascsv[0].split(";");
        
        Integer numerolinhas = linhascsv.length; //numero total de linhas
        Integer numerocolunas = nomescolunas.length; //numero total de colunas
        
        //mierclasses.mcfuncoeshelper.mostrarmensagem(String.valueOf(numerolinhas));
        //mierclasses.mcfuncoeshelper.mostrarmensagem(String.valueOf(numerocolunas));
        //mierclasses.mcfuncoeshelper.mostrarmensagem(String.valueOf(linhascsv[0]));
        //mierclasses.mcfuncoeshelper.mostrarmensagem(String.valueOf(linhascsv[1]));
        
        Object[][] dados = new Object[numerolinhas][numerocolunas];
        for (int i = 1; i < numerolinhas; i++)
        {
            String[] dadoslinha_atual = linhascsv[i].split(";");
            
            for (int j = 0; j < numerocolunas; j++)
            {
                //i-1 porque i comeca em 1. ja que i = 0 eh o nome das colunas
                dados[i-1][j] = dadoslinha_atual[j];
            }
        }
        
        jTableResults.setModel
        (
            new javax.swing.table.DefaultTableModel
            (
                dados,nomescolunas
            )
        );
        jScrollPane1.setViewportView(jTableResults);
    }
    
    void exportardadoscsv()
    {
        try
        {

            //abrir dialog para criar arquivo de save
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogTitle("Please choose a location to export the simulation results .csv file");

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) 
            {
                java.io.File fileToSave = fileChooser.getSelectedFile();

                java.io.PrintWriter writer = new java.io.PrintWriter(fileToSave + ".csv", "UTF-8");
                writer.println(dadoscsvresultado);
                writer.close();
                mierclasses.mcfuncoeshelper.mostrarmensagem(".csv simulation results exported.");
            }
            
        }
        catch (Exception ex)
        {
            mierclasses.mcfuncoeshelper.mostrarmensagem("A problem occurred when exporting. Exception: " + ex.getMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jPanelPai = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelTable = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableResults = new javax.swing.JTable();
        jPanelChart = new javax.swing.JPanel();
        jPanelChartResults = new javax.swing.JPanel();
        jButtonExportCsv = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bot Editor and Simulator Results");

        jPanelPai.setBackground(new java.awt.Color(55, 55, 55));

        jPanelTable.setBackground(new java.awt.Color(55, 55, 55));

        jTableResults.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {"1", "1", "4", "4"},
                {"2", "2", "3", "3"},
                {"3", "3", "2", "2"},
                {"4", "4", "1", "1"}
            },
            new String []
            {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTableResults.setEnabled(false);
        jScrollPane1.setViewportView(jTableResults);

        javax.swing.GroupLayout jPanelTableLayout = new javax.swing.GroupLayout(jPanelTable);
        jPanelTable.setLayout(jPanelTableLayout);
        jPanelTableLayout.setHorizontalGroup(
            jPanelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
        );
        jPanelTableLayout.setVerticalGroup(
            jPanelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Table", jPanelTable);

        jPanelChart.setBackground(new java.awt.Color(55, 55, 55));

        javax.swing.GroupLayout jPanelChartResultsLayout = new javax.swing.GroupLayout(jPanelChartResults);
        jPanelChartResults.setLayout(jPanelChartResultsLayout);
        jPanelChartResultsLayout.setHorizontalGroup(
            jPanelChartResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 650, Short.MAX_VALUE)
        );
        jPanelChartResultsLayout.setVerticalGroup(
            jPanelChartResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 434, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelChartLayout = new javax.swing.GroupLayout(jPanelChart);
        jPanelChart.setLayout(jPanelChartLayout);
        jPanelChartLayout.setHorizontalGroup(
            jPanelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelChartResults, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelChartLayout.setVerticalGroup(
            jPanelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelChartResults, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Chart", jPanelChart);

        jButtonExportCsv.setForeground(new java.awt.Color(0, 0, 255));
        jButtonExportCsv.setText("Export .csv");
        jButtonExportCsv.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButtonExportCsvActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelPaiLayout = new javax.swing.GroupLayout(jPanelPai);
        jPanelPai.setLayout(jPanelPaiLayout);
        jPanelPaiLayout.setHorizontalGroup(
            jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPaiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPaiLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonExportCsv)))
                .addContainerGap())
        );
        jPanelPaiLayout.setVerticalGroup(
            jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPaiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonExportCsv)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelPai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelPai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonExportCsvActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButtonExportCsvActionPerformed
    {//GEN-HEADEREND:event_jButtonExportCsvActionPerformed
            exportardadoscsv();
    }//GEN-LAST:event_jButtonExportCsvActionPerformed

    /**
     * @param args the command line arguments
     */
    public static
            void main(String args[])
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(frame_resultadosbearcodetraderbot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(frame_resultadosbearcodetraderbot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(frame_resultadosbearcodetraderbot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(frame_resultadosbearcodetraderbot.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public
                    void run()
            {
                new frame_resultadosbearcodetraderbot(dadoscsvresultado).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonExportCsv;
    private javax.swing.JPanel jPanelChart;
    private javax.swing.JPanel jPanelChartResults;
    private javax.swing.JPanel jPanelPai;
    private javax.swing.JPanel jPanelTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableResults;
    // End of variables declaration//GEN-END:variables
}
