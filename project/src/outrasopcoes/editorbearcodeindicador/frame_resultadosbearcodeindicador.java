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
package outrasopcoes.editorbearcodeindicador;

import outrasopcoes.editorbearcodetraderbot.*;
import java.awt.BasicStroke;
import java.awt.Color;
import org.jfree.ui.TextAnchor;

/**
 *
 * @author meyerlu
 */


public class frame_resultadosbearcodeindicador extends javax.swing.JFrame
{
    static mierclasses.mcbcindicatorinterpreter mcbciip;
            
    /**
     * Creates new form frame_resultadosbearcodetraderbot
     */
    public frame_resultadosbearcodeindicador(mierclasses.mcbcindicatorinterpreter mcbcii)
    {
        initComponents();

        mcbciip = mcbcii;
        
        criargraficoindicador
        (
            mcbciip.pontosx_lastrun,
            mcbciip.pontosy_lastrun,
            mcbciip.tituloscript_lastrun,
            mcbciip.tipoplot_lastrun
        );
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
    void criargraficoindicador(Object xvalues, Object yvalues, String tituloscript, String desenhografico)
    {

        
        if (desenhografico.equals("drawoncandles"))
        {
            //interpretar valores de x como double, e de y como date
            double[] yvalues_double = (double[]) yvalues;
            java.util.Date[] xvalues_date = (java.util.Date[]) xvalues;

            //criar timeseries com os dados
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
                    executaranotacaofixa_crosshair_mmove(e);
                }
            });

        }
        else if (desenhografico.equals("drawseparateline"))
        {
            //interpretar valores de x como double, e de y como date
            double[] yvalues_double = (double[]) yvalues;
            java.util.Date[] xvalues_date = (java.util.Date[]) xvalues;

            //criar timeseries com os dados
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
            renderer.setBaseStroke(new BasicStroke(2.0f));

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
                    executaranotacaofixa_crosshair_mmove(e);
                }
            });
            
        }
        else if (desenhografico.equals("drawseparatebar"))
        {
            //interpretar valores de x como double, e de y como date
            double[] yvalues_double = (double[]) yvalues;
            java.util.Date[] xvalues_date = (java.util.Date[]) xvalues;

            //criar timeseries com os dados
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
            org.jfree.chart.renderer.xy.XYBarRenderer renderer = new org.jfree.chart.renderer.xy.XYBarRenderer();
            renderer.setDrawBarOutline(true);
            renderer.setBaseStroke(new BasicStroke(10.0f));

            //criar ranges
            org.jfree.chart.axis.DateAxis domainAxis = new org.jfree.chart.axis.DateAxis("");
            org.jfree.chart.axis.NumberAxis rangeAxis = new org.jfree.chart.axis.NumberAxis("");

            //criar xyplot e associar dataset e renderer ao xyplot
            org.jfree.chart.plot.XYPlot plot = new org.jfree.chart.plot.XYPlot(datasettimeseries,domainAxis,rangeAxis,renderer);
            plot.setDataset(datasettimeseries);
            plot.setRenderer(renderer);
            
            //criar chart
            org.jfree.chart.JFreeChart chartseparado = new org.jfree.chart.JFreeChart(tituloscript.toUpperCase(), null,plot,false);

            //criar chartpanel
            chartpanelseparado = new org.jfree.chart.ChartPanel(chartseparado);
            chartpanelseparado.addChartMouseListener(new org.jfree.chart.ChartMouseListener()
            {
                public void chartMouseClicked(org.jfree.chart.ChartMouseEvent e)
                {
                }

                public void chartMouseMoved(org.jfree.chart.ChartMouseEvent e)
                {
                    executaranotacaofixa_crosshair_mmove(e);
                }
            });
        }
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
        jPanelChartResults = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Indicator Editor Results");

        jPanelPai.setBackground(new java.awt.Color(55, 55, 55));

        javax.swing.GroupLayout jPanelChartResultsLayout = new javax.swing.GroupLayout(jPanelChartResults);
        jPanelChartResults.setLayout(jPanelChartResultsLayout);
        jPanelChartResultsLayout.setHorizontalGroup(
            jPanelChartResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 551, Short.MAX_VALUE)
        );
        jPanelChartResultsLayout.setVerticalGroup(
            jPanelChartResultsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 392, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanelPaiLayout = new javax.swing.GroupLayout(jPanelPai);
        jPanelPai.setLayout(jPanelPaiLayout);
        jPanelPaiLayout.setHorizontalGroup(
            jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPaiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelChartResults, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelPaiLayout.setVerticalGroup(
            jPanelPaiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPaiLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelChartResults, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
            java.util.logging.Logger.getLogger(frame_resultadosbearcodeindicador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(frame_resultadosbearcodeindicador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(frame_resultadosbearcodeindicador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(frame_resultadosbearcodeindicador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public
                    void run()
            {
                new frame_resultadosbearcodeindicador(mcbciip).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanelChartResults;
    private javax.swing.JPanel jPanelPai;
    // End of variables declaration//GEN-END:variables
}
