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
        chartpanelseparado = retornarnovoplot_indicador
        (
            mcbciip.pontosx_lastrun,
            mcbciip.pontosy_lastrun,
            mcbciip.tituloscript_lastrun,
            mcbciip.tipoplot_lastrun
        );
        associarplotchartpaneljanela(chartpanelseparado);
    }
    
    void associarplotchartpaneljanela(org.jfree.chart.ChartPanel chartpanel)
    {
        //adicionar chartpanel a jPanelChartpanelHolder
        jPanelChartResults.removeAll();
        jPanelChartResults.setLayout(new java.awt.BorderLayout());
        jPanelChartResults.add(chartpanel);
        //setVisible(true);
        this.validate();
    }

    
    org.jfree.chart.ChartPanel chartpanelseparado = null;
    org.jfree.chart.ChartPanel retornarnovoplot_indicador(Object xvalues, Object yvalues, String tituloscript, String desenhografico)
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
                }
            });
        }
        
        return chartpanelseparado;
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
