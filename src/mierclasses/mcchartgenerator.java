/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mierclasses;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


/**
 *
 * @author lucasmeyer
 */
public class mcchartgenerator
{
    public org.jfree.chart.JFreeChart chartatual; //chart atual
    public org.jfree.chart.ChartPanel chartpanelatual; //chart panel atual
    public java.util.List<mierclasses.mccandle> candlesatual; //lista de candles atual utilizadas por este mcg
    public java.util.List<String> idindicadoresatual; //lista com ids dos indicadores atuais 
    public java.util.List<String> idanotacoesatual; //lista com ids das anotacoes atuais
    
    //informacoes sobre a ultima posicao do mouse no chart
    public double valormouseatualgraficox; //contem o ultimo valor em x que o mouse tocou
    public double valormouseatualgraficoy; //contem o ultimo valor em y que o mouse tocou
    public org.jfree.chart.entity.ChartEntity entityatualgrafico; //contem a ultima entidade que o mouse tocou
    
    //classe que se comunica com jfreecharts para criar graficos de interesse para o programa
    public mcchartgenerator()
    {
        inicializarmcchartgenerator();
    }
    
    void inicializarmcchartgenerator()
    {
        idindicadoresatual = new java.util.ArrayList<>();
        idanotacoesatual = new java.util.ArrayList<>();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Funcoes para funcionamento de ferramentas no chart OHLC">
    
    // <editor-fold defaultstate="collapsed" desc="Troca de Ferramentas">
    public String ferramentaatualgrafico = "selecao"; //variavel que diz qual a ferramenta atual de edicao do grafico
    public void trocarferramentaparaselecao()
    {
        ferramentaatualgrafico = "selecao";
    }
    
    public void trocarferramentaparareta()
    {
        ferramentaatualgrafico = "reta";
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Interpretadores de Eventos">
    void interpretarferramenta_mclick(org.jfree.chart.ChartMouseEvent cmevent)
    {
        //sempre atualizar informacoes sobre a posicao atual do mouse no grafico
        atualizarinformacoesatuaismousegrafico(cmevent);
        
        //funcao para interpretar a ferramenta atual e manipular o grafico
        if (ferramentaatualgrafico.equals("selecao"))
        {
            //no modo de selecao nao eh necessario fazer nada, soh resetar quaisquer ferramentas em uso
            ferramentaselecao_mclick();
        }
        else if (ferramentaatualgrafico.equals("reta"))
        {
            //no modo de reta o usuario clica a primeira vez para dar o primeiro ponto da reta, e uma segunda vez para dar o segundo ponto
            ferramentareta_mclick(cmevent);
        }
    }
    
    void interpretarferramenta_mmove(org.jfree.chart.ChartMouseEvent cmevent)
    {

         //sempre atualizar informacoes sobre a posicao atual do mouse no grafico
        atualizarinformacoesatuaismousegrafico(cmevent);
        
        //funcao para interpretar a ferramenta atual e manipular o grafico
        if (ferramentaatualgrafico.equals("selecao"))
        {
            //no modo de selecao nao eh necessario fazer nada, soh resetar quaisquer ferramentas em uso
            ferramentaselecao_mmove();
        }
        else if (ferramentaatualgrafico.equals("reta"))
        {
            //no modo de reta o usuario clica a primeira vez para dar o primeiro ponto da reta, e uma segunda vez para dar o segundo ponto
            ferramentareta_mmove(cmevent);
        }
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Ferramenta - Selecao">
    void ferramentaselecao_mclick()
    {
        resetarferramentas();
    }
    
    void ferramentaselecao_mmove()
    {
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Ferramenta - Reta">
    boolean ja_tem_ponto_1 = false;
    double reta_x1 = 0;
    double reta_y1 = 0;
    void ferramentareta_mclick(org.jfree.chart.ChartMouseEvent cmevent)
    {
        if (ja_tem_ponto_1 == false)
        {
            reta_x1 = valormouseatualgraficox;
            reta_y1 = valormouseatualgraficoy;
            ja_tem_ponto_1 = true;
        }
        else if (ja_tem_ponto_1 == true)
        {
            ja_tem_ponto_1 = false;
            xylineannotation_preview = null;
            reta_x1 = 0;
            reta_y1 = 0;
        }
    }
    
    org.jfree.chart.annotations.XYLineAnnotation xylineannotation_preview;
    void ferramentareta_mmove(org.jfree.chart.ChartMouseEvent cmevent)
    {
        
        //no caso de reta move, nos queremos que a annotation apareca constantemente como preview ateh o usuario dar o segundo clique
        if (ja_tem_ponto_1 == false)
        {
            //caso nao tenha o primeiro ponto, nao desenhar preview ainda 
        }
        else if (ja_tem_ponto_1 == true)
        {
            //remover annotation
            try
            {
                org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot)chartatual.getPlot();
                plotatual.removeAnnotation(xylineannotation_preview);
            }
            catch (Exception ex)
            {}

            
            //ja tendo o o primeiro ponto, pode desenhar o preview
            double reta_x2_preview = valormouseatualgraficox;
            double reta_y2_preview = valormouseatualgraficoy;
            
            //adicionar annotation line preview nova
            xylineannotation_preview = adicionarplotohlc_annotationreta(reta_x1, reta_y1, reta_x2_preview, reta_y2_preview);

        }
    }
    // </editor-fold>
    
    
    // <editor-fold defaultstate="collapsed" desc="Funcionalidades Core das Ferramentas">
    void atualizarinformacoesatuaismousegrafico(org.jfree.chart.ChartMouseEvent cmevent)
    {
        java.awt.geom.Point2D p = chartpanelatual.translateScreenToJava2D(cmevent.getTrigger().getPoint());
        java.awt.geom.Rectangle2D plotArea = chartpanelatual.getChartRenderingInfo().getPlotInfo().getDataArea();
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) chartatual.getPlot(); // your plot
        valormouseatualgraficox = plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());
        valormouseatualgraficoy = plot.getRangeAxis().java2DToValue(p.getY(), plotArea, plot.getRangeAxisEdge());
        
        entityatualgrafico = cmevent.getEntity();
    }
    
    void resetarferramentas()
    {
        //resetar selecao

        
        //resetar reta
        ja_tem_ponto_1 = false;
        xylineannotation_preview = null;
        reta_x1 = 0;
        reta_y1 = 0;
    }
    
    public void adicionarplotohlc_annotationid(String idadicionar)
    {
        //esta funcao eh necessaria para adicionar o novo id a lista de anotacoes atuais
        //essa adicao deve ser feita manualmente pela logica do core de ferramentas, que funcionam com o mouse
        idanotacoesatual.add(idadicionar);
    }

    public org.jfree.chart.annotations.XYLineAnnotation adicionarplotohlc_annotationreta(double x1, double y1, double x2, double y2)
    { 
        /*
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) chartatual.getXYPlot();
        org.jfree.data.xy.XYSeriesCollection dataset = (org.jfree.data.xy.XYSeriesCollection)plot.getDataset();
        
        org.jfree.chart.annotations.XYLineAnnotation xylineannotation = new org.jfree.chart.annotations.XYLineAnnotation(0, 0, (dataset.getSeries(0)).getMaxX(), (dataset.getSeries(0)).getMaxY(), new BasicStroke(10.0f), Color.red);

        plot.addAnnotation(xylineannotation);
        mcfuncoeshelper.mostrarmensagem("anotacao reta adicionada");
        */
        
        /*
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) chartatual.getXYPlot();
        org.jfree.data.xy.OHLCDataset dataset = (org.jfree.data.xy.OHLCDataset)plot.getDataset();
        
        org.jfree.chart.annotations.XYLineAnnotation xylineannotation = new org.jfree.chart.annotations.XYLineAnnotation(dataset.getXValue(0, 0), dataset.getYValue(0, 0), dataset.getXValue(0, 50), dataset.getYValue(0, 50), new BasicStroke(10.0f), Color.red);

        plot.addAnnotation(xylineannotation);
        mcfuncoeshelper.mostrarmensagem("anotacao reta adicionada");
        */
        
        
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) chartatual.getXYPlot();
        org.jfree.chart.annotations.XYLineAnnotation xylineannotation = new org.jfree.chart.annotations.XYLineAnnotation(x1, y1, x2, y2, new BasicStroke(1.0f), Color.red);

        plot.addAnnotation(xylineannotation);
        return xylineannotation;
        //mierfuncoeshelper.mostrarmensagem("anotacao reta adicionada");
        //funcao retorna a annotation adicionada
    }
    
    public void removerplotohlc_annotation(String idanotacao)
    {
        //encontra o dataset para deletar utilizando o idindicador
        for (int i = 0; i < idanotacoesatual.size(); i++)
        {
            String idanotacaoatual = (String)idanotacoesatual.get(i);

            if (idanotacaoatual.equals(idanotacao))
            {
                java.util.List<Object> lan = retornarlistaanotacoesatuais();
                org.jfree.chart.annotations.XYAnnotation annotationremover = (org.jfree.chart.annotations.XYAnnotation)lan.get(i);
                
                org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot)chartatual.getPlot();
                plotatual.removeAnnotation(annotationremover);
                
                idanotacoesatual.remove(i); //necessario para logica atual
                break;
            }
        }
    }
    

    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Funcoes Helper">
    public java.util.List<Object> retornarlistaanotacoesatuais()
    {
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) chartatual.getXYPlot();
        java.util.List<Object> listaannotations = plot.getAnnotations();
        return listaannotations;
    }
    //</editor-fold>
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Funcoes para funcionamento de indicadores">
    public void adicionarplotohlc_indicador
        (
            Object xvalues,
            Object yvalues,
            String tituloscript,
            String tipoeixoy,
            String desenhografico
        )
    {
        //funcao para adicionar um grafico de indicador sobre o grafico ohlc
        
        if (tipoeixoy.equals("timestamp") && desenhografico.equals("line"))
        {
            //interpretar valores de x como double, e de y como date
            double[] yvalues_double = (double[])yvalues;
            java.util.Date[] xvalues_date = (java.util.Date[])xvalues;


            //pegar plot do chartpanel
            org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot)chartatual.getPlot();

            //criar dataset timeseries com os dados
            org.jfree.data.time.TimeSeriesCollection datasetadd = new org.jfree.data.time.TimeSeriesCollection();
            org.jfree.data.time.TimeSeries seriesadd = new org.jfree.data.time.TimeSeries(tituloscript);

            for (int i = 0; i < yvalues_double.length; i++)
            {

                org.jfree.data.time.Millisecond millisegundoatual = 
                        new org.jfree.data.time.Millisecond(xvalues_date[i]);

                double valoratual = yvalues_double[i];

                seriesadd.add(millisegundoatual,valoratual);
            }
            datasetadd.addSeries(seriesadd);

            //criar um line renderer
            org.jfree.chart.renderer.xy.XYLineAndShapeRenderer rendereradd = new org.jfree.chart.renderer.xy.DefaultXYItemRenderer();
            rendereradd.setBaseShapesVisible(false);
            rendereradd.setBaseStroke(new BasicStroke(2.0f));
            
            int numerodatasetsatual = plotatual.getDatasetCount();
            plotatual.setDataset(numerodatasetsatual,datasetadd);
            plotatual.setRenderer(numerodatasetsatual, rendereradd);
        }
    }
        
    public void adicionarplotohlc_indicadorid(String idindicador)
    {
        idindicadoresatual.add(idindicador);
    }
    
    public void removerplotohlc_indicador(String idindicador)
    {
        //encontra o dataset para deletar utilizando o idindicador
        for (int i = 0; i < idindicadoresatual.size(); i++)
        {
            String idindicadoratual = (String)idindicadoresatual.get(i);
            
            if (idindicador.equals(idindicadoratual))
            {
                org.jfree.chart.plot.XYPlot plotatual = (org.jfree.chart.plot.XYPlot)chartatual.getPlot();
                org.jfree.data.time.TimeSeriesCollection datasetatual = (org.jfree.data.time.TimeSeriesCollection) plotatual.getDataset(i+1); //i+1 porque o 0 eh o proprio ohlc
                datasetatual.removeAllSeries();
                
                //idindicadoresatual.remove(i); //nao fazer!
            }
        }
    }
        
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Funcoes para recriar e retornar chart OHLC com indicadores">
    public void recriarohlc(java.util.List<mierclasses.mccandle> catual, mierclasses.mcavsearchresultcandle infosimbolo)
    {
        //limpar lista de id de indicadores considerando que todos os indicadores
        //e anotacoes serao deletados e um novo OLHC chart sera criado
        idindicadoresatual = new java.util.ArrayList<>();
        idanotacoesatual = new java.util.ArrayList<>();
        
        // <editor-fold defaultstate="collapsed" desc="Recriar Grafico OHLC">
        //grafico ohlc
        candlesatual = catual;
        
        //criar dataset
        org.jfree.data.xy.OHLCDataset olhcdataset = criarohlcdataset(candlesatual,infosimbolo);
    
        //criar grafico
        org.jfree.chart.JFreeChart chart = org.jfree.chart.ChartFactory.createHighLowChart
        (
            infosimbolo.symbolstr + " - " + infosimbolo.namestr, 
            "Data", 
            "Preco",
            olhcdataset, 
            true
        );
        
        org.jfree.chart.plot.XYPlot plot = (org.jfree.chart.plot.XYPlot) chart.getPlot();
        org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
        org.jfree.chart.renderer.xy.HighLowRenderer renderer = (org.jfree.chart.renderer.xy.HighLowRenderer) plot.getRenderer();
        renderer.setBaseStroke(new BasicStroke(2.0f));
        renderer.setSeriesPaint(0, Color.blue);
 
        rangeAxis.setAutoRangeIncludesZero(false);
        chartatual = chart;
        
        
        // Create Panel
        org.jfree.chart.ChartPanel chartpanel = new org.jfree.chart.ChartPanel(chartatual);
        chartpanel.addChartMouseListener(new org.jfree.chart.ChartMouseListener()
        {
            public void chartMouseClicked(org.jfree.chart.ChartMouseEvent e)
            {
                //mierfuncoeshelper.mostrarmensagem("OLA");
                interpretarferramenta_mclick(e);
            }
            
            public void chartMouseMoved(org.jfree.chart.ChartMouseEvent e) 
            {
                interpretarferramenta_mmove(e);
            }
        });
        chartpanelatual = chartpanel;
        //</editor-fold>

    }

    public org.jfree.chart.ChartPanel retornarcpanelohlc()
    {
        return chartpanelatual;
    }
    
    org.jfree.data.xy.OHLCDataset criarohlcdataset(java.util.List<mierclasses.mccandle> candles, mierclasses.mcavsearchresultcandle infosimbolo)
    {
        java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
        //org.jfree.data.xy.OHLCDataItem dataItem[] = null;
        
        java.util.List<org.jfree.data.xy.OHLCDataItem> listohlcitems = new java.util.ArrayList<>();
        
        for (int i = 0; i < candles.size(); i++)
        {
            mierclasses.mccandle candleatual = candles.get(i);
            
            org.jfree.data.xy.OHLCDataItem dataitemadd = 
                    new org.jfree.data.xy.OHLCDataItem
                    (
                            candleatual.timestampdate,
                            candleatual.opend,
                            candleatual.highd,
                            candleatual.lowd,
                            candleatual.closed,
                            candleatual.volumed
                    );
           
            
            listohlcitems.add(dataitemadd);
        }
        
        org.jfree.data.xy.OHLCDataItem[] arrayohlcitems = new org.jfree.data.xy.OHLCDataItem[listohlcitems.size()];
        arrayohlcitems = listohlcitems.toArray(arrayohlcitems);
        
        org.jfree.data.xy.OHLCDataset datasetretornar = new org.jfree.data.xy.DefaultOHLCDataset(infosimbolo.symbolstr + " - " + infosimbolo.namestr, arrayohlcitems);
        
        return datasetretornar;
    }

    // </editor-fold>
    
}