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
package analisadorasset.submodulografico;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;

/**
 *
 * @author lucasmeyer
 */
public class frame_chartseparado extends javax.swing.JFrame
{

    /**
     * Creates new form mfchartseparado
     */
    public frame_chartseparado()
    {
        initComponents();
    }
    
    public void associarplotchartpaneljanela(org.jfree.chart.ChartPanel chartpanel)
    {
        //adicionar chartpanel a jPanelChartpanelHolder
        jPanelChartpanelholder.removeAll();
        jPanelChartpanelholder.setLayout(new java.awt.BorderLayout());
        jPanelChartpanelholder.add(chartpanel);
        //setVisible(true);
        this.validate();
    }

    
    org.jfree.chart.ChartPanel chartpanelseparado = null;
    public org.jfree.chart.ChartPanel retornarnovoplot_indicador(Object xvalues, Object yvalues, String tituloscript, String desenhografico)
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

        jPanelChartpanelholder = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Indicator");

        jPanelChartpanelholder.setBackground(new java.awt.Color(0, 0, 0));
        jPanelChartpanelholder.setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));

        javax.swing.GroupLayout jPanelChartpanelholderLayout = new javax.swing.GroupLayout(jPanelChartpanelholder);
        jPanelChartpanelholder.setLayout(jPanelChartpanelholderLayout);
        jPanelChartpanelholderLayout.setHorizontalGroup(
            jPanelChartpanelholderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 510, Short.MAX_VALUE)
        );
        jPanelChartpanelholderLayout.setVerticalGroup(
            jPanelChartpanelholderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 258, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 530, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanelChartpanelholder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 280, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanelChartpanelholder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
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
        } catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(frame_chartseparado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(frame_chartseparado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(frame_chartseparado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(frame_chartseparado.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new frame_chartseparado().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanelChartpanelholder;
    // End of variables declaration//GEN-END:variables
}
