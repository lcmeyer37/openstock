/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mierclasses;

import org.json.JSONArray;
import org.json.JSONObject;

//classe para interpretacao de informacoes json recebidas pelo alpha vantage api
/**
 *
 * @author lucasmeyer
 */
public class mcavcomms
{
    //classe utilizada para comunicacao web
    mcwebcomms mwcomms;
    
    //chave da api alphavantage
    String chavealphavantage = "";
    
    public mcavcomms(String chaveav)
    {
        chavealphavantage = chaveav;
        mwcomms = new mcwebcomms();
    }
    
    public void testecomunicacaoav()
    {
        String conteudopagina = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=MSFT&apikey=" + chavealphavantage);
        mierclasses.mcfuncoeshelper.mostrarmensagem(conteudopagina);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Funcoes para resgate de informacao do Alpha Vantage">

    // <editor-fold defaultstate="collapsed" desc="Stock Time Series">

    public java.util.List<mccandle> recebermfxtestcandles()
    {
        //em teste
        //funcao para retornar candles de teste
        
        java.util.List<mccandle> listacandlesretornar = new java.util.ArrayList<>();
        
        for (int i = 0; i < 50; i++)
        {
            java.util.Date datagerada = new java.util.Date(1993,04,30);
            datagerada = new java.util.Date(datagerada.getTime() + 86400000 * i); //gerar dias apartir de 30 de abril de 1993
            
            String timestampcandle = datagerada.getYear() + "-" + datagerada.getMonth() + "-" + datagerada.getDate();
            //System.out.println(timestampcandle);
            
            double sinvalue = java.lang.Math.sin(i);
            double opend = sinvalue * 10;
            double lowd = opend - 15;
            double closed = opend - 10;
            double highd = opend + 15;
            double volumed = sinvalue * 1000;

            
            String opencandle = String.valueOf(opend);
            //System.out.println(opencandle);
            String highcandle = String.valueOf(highd);
            String lowcandle = String.valueOf(lowd);
            String closecandle = String.valueOf(closed);
            String volumecandle= String.valueOf(volumed);
            
            // public mccandle(String tsstr, String ostr, String hstr, String cstr, String lstr, String vstr)
            mccandle candleatual = new mccandle(timestampcandle,opencandle,highcandle,closecandle,lowcandle,volumecandle);
            listacandlesretornar.add(candleatual);
        }
   
        return listacandlesretornar;
    }
    
    
    public java.util.List<mccandle> receberstockcandlesintraday(String simbolo, String intervalo, String outputsize)
    {
                    /*
        {
    "Meta Data": {
        "1. Information": "Daily Prices (open, high, low, close) and Volumes",
        "2. Symbol": "MSFT",
        "3. Last Refreshed": "2018-12-21",
        "4. Output Size": "Compact",
        "5. Time Zone": "US/Eastern"
    },
    "Time Series (Daily)": {
        "2018-12-21": {
            "1. open": "101.6300",
            "2. high": "103.0000",
            "3. low": "97.4600",
            "4. close": "98.2300",
            "5. volume": "111242070"
        },
        "2018-12-20": {
            "1. open": "103.0500",
            "2. high": "104.3100",
            "3. low": "98.7800",
            "4. close": "101.5100",
            "5. volume": "70334184"
        },
        */
        
        String jsonconteudo = "";
        if (outputsize.length() == 0)
            jsonconteudo = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol="+simbolo+"&interval="+intervalo+"&apikey=" + chavealphavantage);
        else if (outputsize.length() > 0)
            jsonconteudo = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol="+simbolo+"&interval="+intervalo+"&outputsize="+outputsize+"&apikey=" + chavealphavantage);
        
        JSONObject obj = new JSONObject(jsonconteudo);
        
        String md_information = obj.getJSONObject("Meta Data").getString("1. Information");
        String md_symbol = obj.getJSONObject("Meta Data").getString("2. Symbol");
        String md_lastrefreshed = obj.getJSONObject("Meta Data").getString("3. Last Refreshed");
        String md_interval = obj.getJSONObject("Meta Data").getString("4. Interval");
        String md_outputsize = obj.getJSONObject("Meta Data").getString("5. Output Size");
        String md_timezone = obj.getJSONObject("Meta Data").getString("6. Time Zone");


        JSONObject candlesjson = obj.getJSONObject("Time Series (" +intervalo+ ")");
        JSONArray candlesjsontimestamparray = candlesjson.names();
        
        
        java.util.List<mccandle> listacandlesretornar = new java.util.ArrayList<>();
        
        for (int i = 0; i < candlesjsontimestamparray.length(); i++)
        {
            String timestampcandle = candlesjsontimestamparray.getString(i);
            //System.out.println(timestampcandle);
            
            String opencandle = candlesjson.getJSONObject(timestampcandle).getString("1. open");
            //System.out.println(opencandle);
            String highcandle = candlesjson.getJSONObject(timestampcandle).getString("2. high");
            String lowcandle = candlesjson.getJSONObject(timestampcandle).getString("3. low");
            String closecandle = candlesjson.getJSONObject(timestampcandle).getString("4. close");
            String volumecandle = candlesjson.getJSONObject(timestampcandle).getString("5. volume");
            
            // public mccandle(String tsstr, String ostr, String hstr, String cstr, String lstr, String vstr)
            mccandle candleatual = new mccandle(timestampcandle,opencandle,highcandle,closecandle,lowcandle,volumecandle);
            listacandlesretornar.add(candleatual);
        }
   
        return listacandlesretornar;
    }
    
    public java.util.List<mccandle> receberstockcandlesdaily(String simbolo, String outputsize)
    {

        String jsonconteudo = "";
        if (outputsize.length() == 0)
            jsonconteudo = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="+simbolo+"&apikey=" + chavealphavantage);
        else if (outputsize.length() > 0)
            jsonconteudo = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol="+simbolo+"&outputsize="+outputsize+"&apikey=" + chavealphavantage);
        
        JSONObject obj = new JSONObject(jsonconteudo);
        
        String md_information = obj.getJSONObject("Meta Data").getString("1. Information");
        String md_symbol = obj.getJSONObject("Meta Data").getString("2. Symbol");
        String md_lastrefreshed = obj.getJSONObject("Meta Data").getString("3. Last Refreshed");
        String md_outputsize = obj.getJSONObject("Meta Data").getString("4. Output Size");
        String md_timezone = obj.getJSONObject("Meta Data").getString("5. Time Zone");


        JSONObject candlesjson = obj.getJSONObject("Time Series (Daily)");
        JSONArray candlesjsontimestamparray = candlesjson.names();
        
        
        java.util.List<mccandle> listacandlesretornar = new java.util.ArrayList<>();
        
        for (int i = 0; i < candlesjsontimestamparray.length(); i++)
        {
            String timestampcandle = candlesjsontimestamparray.getString(i);
            
            String opencandle = candlesjson.getJSONObject(timestampcandle).getString("1. open");
            String highcandle = candlesjson.getJSONObject(timestampcandle).getString("2. high");
            String lowcandle = candlesjson.getJSONObject(timestampcandle).getString("3. low");
            String closecandle = candlesjson.getJSONObject(timestampcandle).getString("4. close");
            String volumecandle = candlesjson.getJSONObject(timestampcandle).getString("5. volume");
            
            mccandle candleatual = new mccandle(timestampcandle,opencandle,highcandle,closecandle,lowcandle,volumecandle);
            listacandlesretornar.add(candleatual);
        }
   
        return listacandlesretornar;
    }
    
    public java.util.List<mccandle> receberstockcandlesdailyadjusted(String simbolo, String outputsize)
    {

        String jsonconteudo = "";
        if (outputsize.length() == 0)
            jsonconteudo = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol="+simbolo+"&apikey=" + chavealphavantage);
        else if (outputsize.length() > 0)
            jsonconteudo = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol="+simbolo+"&outputsize="+outputsize+"&apikey=" + chavealphavantage);
        
        JSONObject obj = new JSONObject(jsonconteudo);
        
        String md_information = obj.getJSONObject("Meta Data").getString("1. Information");
        String md_symbol = obj.getJSONObject("Meta Data").getString("2. Symbol");
        String md_lastrefreshed = obj.getJSONObject("Meta Data").getString("3. Last Refreshed");
        String md_outputsize = obj.getJSONObject("Meta Data").getString("4. Output Size");
        String md_timezone = obj.getJSONObject("Meta Data").getString("5. Time Zone");


        JSONObject candlesjson = obj.getJSONObject("Time Series (Daily)");
        JSONArray candlesjsontimestamparray = candlesjson.names();
        
        
        java.util.List<mccandle> listacandlesretornar = new java.util.ArrayList<>();
        
        for (int i = 0; i < candlesjsontimestamparray.length(); i++)
        {
            String timestampcandle = candlesjsontimestamparray.getString(i);
            
            String opencandle = candlesjson.getJSONObject(timestampcandle).getString("1. open");
            String highcandle = candlesjson.getJSONObject(timestampcandle).getString("2. high");
            String lowcandle = candlesjson.getJSONObject(timestampcandle).getString("3. low");
            String closecandle = candlesjson.getJSONObject(timestampcandle).getString("4. close");
            String volumecandle = candlesjson.getJSONObject(timestampcandle).getString("5. volume");
            
            mccandle candleatual = new mccandle(timestampcandle,opencandle,highcandle,closecandle,lowcandle,volumecandle);
            listacandlesretornar.add(candleatual);
        }
   
        return listacandlesretornar;
    }
    
    public java.util.List<mccandle> receberstockcandlesweekly(String simbolo, String outputsize)
    {

        String jsonconteudo = "";
        if (outputsize.length() == 0)
            jsonconteudo = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY&symbol="+simbolo+"&apikey=" + chavealphavantage);
        else if (outputsize.length() > 0)
            jsonconteudo = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY&symbol="+simbolo+"&outputsize="+outputsize+"&apikey=" + chavealphavantage);
        
        JSONObject obj = new JSONObject(jsonconteudo);
        
        String md_information = obj.getJSONObject("Meta Data").getString("1. Information");
        String md_symbol = obj.getJSONObject("Meta Data").getString("2. Symbol");
        String md_lastrefreshed = obj.getJSONObject("Meta Data").getString("3. Last Refreshed");
        String md_timezone = obj.getJSONObject("Meta Data").getString("4. Time Zone");


        JSONObject candlesjson = obj.getJSONObject("Weekly Time Series");
        JSONArray candlesjsontimestamparray = candlesjson.names();
        
        
        java.util.List<mccandle> listacandlesretornar = new java.util.ArrayList<>();
        
        for (int i = 0; i < candlesjsontimestamparray.length(); i++)
        {
            String timestampcandle = candlesjsontimestamparray.getString(i);
            
            String opencandle = candlesjson.getJSONObject(timestampcandle).getString("1. open");
            String highcandle = candlesjson.getJSONObject(timestampcandle).getString("2. high");
            String lowcandle = candlesjson.getJSONObject(timestampcandle).getString("3. low");
            String closecandle = candlesjson.getJSONObject(timestampcandle).getString("4. close");
            String volumecandle = candlesjson.getJSONObject(timestampcandle).getString("5. volume");
            
            mccandle candleatual = new mccandle(timestampcandle,opencandle,highcandle,closecandle,lowcandle,volumecandle);
            listacandlesretornar.add(candleatual);
        }
   
        return listacandlesretornar;
    }
    
    public java.util.List<mccandle> receberstockcandlesweeklyadjusted(String simbolo, String outputsize)
    {

        String jsonconteudo = "";
        if (outputsize.length() == 0)
            jsonconteudo = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY_ADJUSTED&symbol="+simbolo+"&apikey=" + chavealphavantage);
        else if (outputsize.length() > 0)
            jsonconteudo = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY_ADJUSTED&symbol="+simbolo+"&outputsize="+outputsize+"&apikey=" + chavealphavantage);
        
        JSONObject obj = new JSONObject(jsonconteudo);
        
        String md_information = obj.getJSONObject("Meta Data").getString("1. Information");
        String md_symbol = obj.getJSONObject("Meta Data").getString("2. Symbol");
        String md_lastrefreshed = obj.getJSONObject("Meta Data").getString("3. Last Refreshed");
        String md_timezone = obj.getJSONObject("Meta Data").getString("4. Time Zone");


        JSONObject candlesjson = obj.getJSONObject("Weekly Adjusted Time Series");
        JSONArray candlesjsontimestamparray = candlesjson.names();
        
        
        java.util.List<mccandle> listacandlesretornar = new java.util.ArrayList<>();
        
        for (int i = 0; i < candlesjsontimestamparray.length(); i++)
        {
            String timestampcandle = candlesjsontimestamparray.getString(i);
            
            String opencandle = candlesjson.getJSONObject(timestampcandle).getString("1. open");
            String highcandle = candlesjson.getJSONObject(timestampcandle).getString("2. high");
            String lowcandle = candlesjson.getJSONObject(timestampcandle).getString("3. low");
            String closecandle = candlesjson.getJSONObject(timestampcandle).getString("4. close");
            String volumecandle = candlesjson.getJSONObject(timestampcandle).getString("5. volume");
            
            mccandle candleatual = new mccandle(timestampcandle,opencandle,highcandle,closecandle,lowcandle,volumecandle);
            listacandlesretornar.add(candleatual);
        }
   
        return listacandlesretornar;
    }
    
    public java.util.List<mccandle> receberstockcandlesmonthly(String simbolo, String outputsize)
    {

        String jsonconteudo = "";
        if (outputsize.length() == 0)
            jsonconteudo = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol="+simbolo+"&apikey=" + chavealphavantage);
        else if (outputsize.length() > 0)
            jsonconteudo = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY&symbol="+simbolo+"&outputsize="+outputsize+"&apikey=" + chavealphavantage);
        
        JSONObject obj = new JSONObject(jsonconteudo);
        
        String md_information = obj.getJSONObject("Meta Data").getString("1. Information");
        String md_symbol = obj.getJSONObject("Meta Data").getString("2. Symbol");
        String md_lastrefreshed = obj.getJSONObject("Meta Data").getString("3. Last Refreshed");
        String md_timezone = obj.getJSONObject("Meta Data").getString("4. Time Zone");


        JSONObject candlesjson = obj.getJSONObject("Monthly Time Series");
        JSONArray candlesjsontimestamparray = candlesjson.names();
        
        
        java.util.List<mccandle> listacandlesretornar = new java.util.ArrayList<>();
        
        for (int i = 0; i < candlesjsontimestamparray.length(); i++)
        {
            String timestampcandle = candlesjsontimestamparray.getString(i);
            
            String opencandle = candlesjson.getJSONObject(timestampcandle).getString("1. open");
            String highcandle = candlesjson.getJSONObject(timestampcandle).getString("2. high");
            String lowcandle = candlesjson.getJSONObject(timestampcandle).getString("3. low");
            String closecandle = candlesjson.getJSONObject(timestampcandle).getString("4. close");
            String volumecandle = candlesjson.getJSONObject(timestampcandle).getString("5. volume");
            
            mccandle candleatual = new mccandle(timestampcandle,opencandle,highcandle,closecandle,lowcandle,volumecandle);
            listacandlesretornar.add(candleatual);
        }
   
        return listacandlesretornar;
    }
    
    public java.util.List<mccandle> receberstockcandlesmonthlyadjusted(String simbolo, String outputsize)
    {

        String jsonconteudo = "";
        if (outputsize.length() == 0)
            jsonconteudo = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY_ADJUSTED&symbol="+simbolo+"&apikey=" + chavealphavantage);
        else if (outputsize.length() > 0)
            jsonconteudo = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=TIME_SERIES_MONTHLY_ADJUSTED&symbol="+simbolo+"&outputsize="+outputsize+"&apikey=" + chavealphavantage);
        
        JSONObject obj = new JSONObject(jsonconteudo);
        
        String md_information = obj.getJSONObject("Meta Data").getString("1. Information");
        String md_symbol = obj.getJSONObject("Meta Data").getString("2. Symbol");
        String md_lastrefreshed = obj.getJSONObject("Meta Data").getString("3. Last Refreshed");
        String md_timezone = obj.getJSONObject("Meta Data").getString("4. Time Zone");


        JSONObject candlesjson = obj.getJSONObject("Monthly Adjusted Time Series");
        JSONArray candlesjsontimestamparray = candlesjson.names();
        
        
        java.util.List<mccandle> listacandlesretornar = new java.util.ArrayList<>();
        
        for (int i = 0; i < candlesjsontimestamparray.length(); i++)
        {
            String timestampcandle = candlesjsontimestamparray.getString(i);
            
            String opencandle = candlesjson.getJSONObject(timestampcandle).getString("1. open");
            String highcandle = candlesjson.getJSONObject(timestampcandle).getString("2. high");
            String lowcandle = candlesjson.getJSONObject(timestampcandle).getString("3. low");
            String closecandle = candlesjson.getJSONObject(timestampcandle).getString("4. close");
            String volumecandle = candlesjson.getJSONObject(timestampcandle).getString("5. volume");
            
            mccandle candleatual = new mccandle(timestampcandle,opencandle,highcandle,closecandle,lowcandle,volumecandle);
            listacandlesretornar.add(candleatual);
        }
   
        return listacandlesretornar;
    }
    
    public mcglobalquote receberstockglobalquote(String simbolo)
    {

        String jsonconteudo = "";
        jsonconteudo = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="+simbolo+"&apikey=" + chavealphavantage);
    
        JSONObject obj = new JSONObject(jsonconteudo);
        
        String gq_symbol = obj.getJSONObject("Global Quote").getString("01. symbol");
        String gq_open = obj.getJSONObject("Global Quote").getString("02. open");
        String gq_high = obj.getJSONObject("Global Quote").getString("03. high");
        String gq_low = obj.getJSONObject("Global Quote").getString("04. low");
        String gq_price = obj.getJSONObject("Global Quote").getString("05. price");
        String gq_volume = obj.getJSONObject("Global Quote").getString("06. volume");
        String gq_ltday = obj.getJSONObject("Global Quote").getString("07. latest trading day");
        String gq_pclose = obj.getJSONObject("Global Quote").getString("08. previous close");
        String gq_change = obj.getJSONObject("Global Quote").getString("09. change");
        String gq_changepercent = obj.getJSONObject("Global Quote").getString("10. change percent");

        /*
        public mcglobalquote
        (String sstr, String ostr, String hstr, String lstr, String pstr, String vstr, String ltdstr,
            String pcstr, String chstr, String chpstr)
    {
        */
        
       mcglobalquote gquoteretornar = new mcglobalquote(gq_symbol,gq_open,gq_high,gq_low,gq_price,gq_volume,gq_ltday,
       gq_pclose,gq_change,gq_changepercent);
       
       return gquoteretornar;
    }
    
    public java.util.List<mcavsearchresultcandle> receberstocksearchbestmatches(String palavrabuscar)
    {
        
        String jsonconteudo = "";
        jsonconteudo = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords="+palavrabuscar+"&apikey=" + chavealphavantage);
        //mierclasses.mcfuncoeshelper.mostrarmensagem(jsonconteudo);
        //mierclasses.mcfuncoeshelper.setarclipboard(jsonconteudo);
        
        JSONObject obj = new JSONObject(jsonconteudo);
        
        JSONArray matchesarray = obj.getJSONArray("bestMatches");
        
        java.util.List<mcavsearchresultcandle> listamatchscores = new java.util.ArrayList<>();

        for (int i = 0; i < matchesarray.length(); i++) 
        {
            String bm_symbol = matchesarray.getJSONObject(i).getString("1. symbol");
            String bm_name = matchesarray.getJSONObject(i).getString("2. name");
            String bm_type = matchesarray.getJSONObject(i).getString("3. type");
            String bm_region = matchesarray.getJSONObject(i).getString("4. region");
            String bm_marketopen = matchesarray.getJSONObject(i).getString("5. marketOpen");
            String bm_marketclose = matchesarray.getJSONObject(i).getString("6. marketClose");
            String bm_timezone = matchesarray.getJSONObject(i).getString("7. timezone");
            String bm_currency = matchesarray.getJSONObject(i).getString("8. currency");
            String bm_matchscore = matchesarray.getJSONObject(i).getString("9. matchScore");
            
            mcavsearchresultcandle mbm = new mcavsearchresultcandle(bm_symbol,bm_name,bm_type,bm_region,bm_marketopen,
            bm_marketclose,bm_timezone,bm_currency,bm_matchscore);
            
            listamatchscores.add(mbm);
        }

        return listamatchscores;
    }
    
    // </editor-fold>
    
    // </editor-fold>
    

}    
    
    // <editor-fold defaultstate="collapsed" desc="Exemplo JSON parse">
                
    
    
    /*
    
    {
   "pageInfo": {
         "pageName": "Homepage",
         "logo": "https://www.example.com/logo.jpg"
    },
    "posts": [
         {
              "post_id": "0123456789",
              "actor_id": "1001",
              "author_name": "Jane Doe",
              "post_title": "How to parse JSON in Java",
              "comments": [],
              "time_of_post": "1234567890"
         }
    ]
}
    
    
    import org.json.JSONArray;
import org.json.JSONObject;

public class ParseJSON {
    static String json = "...";
    public static void main(String[] args) {
        JSONObject obj = new JSONObject(json);
        String pageName = obj.getJSONObject("pageInfo").getString("pageName");

        System.out.println(pageName);

        JSONArray arr = obj.getJSONArray("posts");
        for (int i = 0; i < arr.length(); i++) {
            String post_id = arr.getJSONObject(i).getString("post_id");
            System.out.println(post_id);
        }
    }
}
    
    */
    // </editor-fold>
    


