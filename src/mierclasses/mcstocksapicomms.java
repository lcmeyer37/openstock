/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mierclasses;

//classe para interpretacao de informacoes json recebidas pelo IEX api

import org.json.JSONArray;
import org.json.JSONObject;

//https://iextrading.com/developer/docs/#getting-started
/**
 *
 * @author lucasmeyer
 */
public class mcstocksapicomms
{
    //classe utilizada para comunicacao web
    mcwebcomms mwcomms;
    
    public mcstocksapicomms()
    {
        mwcomms = new mcwebcomms();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Alpha Vantage API">
    String chavealphavantage = "";
    public void alterarchaveav(String novachaveav)
    {
        chavealphavantage = novachaveav;
    }
    
    public void testecomunicacaoav()
    {
        String conteudopagina = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=MSFT&apikey=" + chavealphavantage);
        mierclasses.mcfuncoeshelper.mostrarmensagem(conteudopagina);
    }
    
    public java.util.List<String> receberlistasimbolosavprocura(String parametrobusca)
    {
        String jsonconteudo = "";
        jsonconteudo = mwcomms.receberconteudopagina("https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords="+parametrobusca+"&apikey=" + chavealphavantage);
        //mierclasses.mcfuncoeshelper.mostrarmensagem(jsonconteudo);
        //mierclasses.mcfuncoeshelper.setarclipboard(jsonconteudo);
        
        JSONObject obj = new JSONObject(jsonconteudo);
        
        JSONArray matchesarray = obj.getJSONArray("bestMatches");
        
        java.util.List<String> listasimbolosencontrada = new java.util.ArrayList<>();
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
            
            //mierclasses.mcfuncoeshelper.mostrarmensagem(bm_symbol + " - " + bm_name + " (" + bm_type + ")");
            if ((bm_symbol.toLowerCase()).contains(parametrobusca.toLowerCase()))
            {
                String simboloadd = "AV:" + bm_symbol + " - " + bm_name + " (" + bm_type + ")";
                listasimbolosencontrada.add(simboloadd);
            }
            else if ((bm_name.toLowerCase()).contains(parametrobusca.toLowerCase()))
            {
                String simboloadd = "AV:" + bm_symbol + " - " + bm_name + " (" + bm_type + ")";
                listasimbolosencontrada.add(simboloadd);
            }
        }

        return listasimbolosencontrada;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Stock Time Series">
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

    // </editor-fold>
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="CryptoCompare API">
    String chavecryptocompare = "";
    public void alterarchavecc(String novachavecc)
    {
        chavecryptocompare = novachavecc;
    }
    
     public java.util.List<String> receberlistasimboloscryptocompareprocura(String parametrobusca)
    {
        /*
        {
            "Response": "Success",
            "Message": "Coin list succesfully returned!",
            "Data": {
                "42": {
                    "Id": "4321",
                    "Url": "/coins/42/overview",
                    "ImageUrl": "/media/12318415/42.png",
                    "Name": "42",
                    "Symbol": "42",
                    "CoinName": "42 Coin",
                    "FullName": "42 Coin (42)",
                    "Algorithm": "Scrypt",
                    "ProofType": "PoW/PoS",
                    "FullyPremined": "0",
                    "TotalCoinSupply": "42",
                    "BuiltOn": "N/A",
                    "SmartContractAddress": "N/A",
                    "PreMinedValue": "N/A",
                    "TotalCoinsFreeFloat": "N/A",
                    "SortOrder": "34",
                    "Sponsored": false,
                    "IsTrading": true,
                    "TotalCoinsMined": 41.99995653,
                    "BlockNumber": 0,
                    "NetHashesPerSecond": 0,
                    "BlockReward": 0,
                    "BlockTime": 0
                },
                "ETH": {
                    "Id": "7605",
                    "Url": "/coins/eth/overview",
                    "ImageUrl": "/media/20646/eth_logo.png",
                    "Name": "ETH",
                    "Symbol": "ETH",
                    "CoinName": "Ethereum",
                    "FullName": "Ethereum (ETH)",
                    "Algorithm": "Ethash",
                    "ProofType": "PoW",
                    "FullyPremined": "0",
                    "TotalCoinSupply": "0",
                    "BuiltOn": "N/A",
                    "SmartContractAddress": "N/A",
                    "PreMinedValue": "N/A",
                    "TotalCoinsFreeFloat": "N/A",
                    "SortOrder": "2",
                    "Sponsored": false,
                    "IsTrading": true,
                    "TotalCoinsMined": 104716377.6866,
                    "BlockNumber": 7174858,
                    "NetHashesPerSecond": 140129872071527,
                    "BlockReward": 3,
                    "BlockTime": 15
                }
            },
            "BaseImageUrl": "https://www.cryptocompare.com",
            "BaseLinkUrl": "https://www.cryptocompare.com",
            "RateLimit": {},
            "HasWarning": false,
            "Type": 100
        }
        */
        
        String jsonconteudo = "";
        jsonconteudo = mwcomms.receberconteudopagina("https://min-api.cryptocompare.com/data/all/coinlist");
        //mierclasses.mcfuncoeshelper.mostrarmensagem(jsonconteudo);
        //mierclasses.mcfuncoeshelper.setarclipboard(jsonconteudo);
        
        JSONObject obj = new JSONObject(jsonconteudo);
        
        JSONObject todossimbolos = obj.getJSONObject("Data");
        JSONArray todossimbolosarray = todossimbolos.names();
        
        java.util.List<String> listasimbolosencontrada = new java.util.ArrayList<>();
        for (int i = 0; i < todossimbolosarray.length(); i++)
        {
            String simboloatual = todossimbolosarray.getString(i);
            //System.out.println(timestampcandle);

            String symbol = todossimbolos.getJSONObject(simboloatual).getString("Symbol");
            String name = todossimbolos.getJSONObject(simboloatual).getString("CoinName");
            
            if ((symbol.toLowerCase()).contains(parametrobusca.toLowerCase()))
            {
                String simboloadd = "CRYCOM:" + symbol + " - " + name + " (USD)";
                listasimbolosencontrada.add(simboloadd);
            }
            else if ((name.toLowerCase()).contains(parametrobusca.toLowerCase()))
            {
                String simboloadd = "CRYCOM:" + symbol + " - " + name+ " (USD)";
                listasimbolosencontrada.add(simboloadd);
            }
        }
        
        return listasimbolosencontrada;
    }
    
    public java.util.List<mccandle> recebercryptochartusd(String simbolo, String limite, String periodo)
    {
        
        String caminhourl = "";
        if (periodo.equals("Daily"))
            caminhourl = "https://min-api.cryptocompare.com/data/histoday?fsym="+simbolo.toUpperCase()+"&tsym=USD&limit=" + limite + "&api_key=" + chavecryptocompare;
        else if (periodo.equals("Hourly"))
            caminhourl = "https://min-api.cryptocompare.com/data/histohour?fsym="+simbolo.toUpperCase()+"&tsym=USD&limit=" + limite + "&api_key=" + chavecryptocompare;
        else if (periodo.equals("Minute"))
            caminhourl = "https://min-api.cryptocompare.com/data/histominute?fsym="+simbolo.toUpperCase()+"&tsym=USD&limit=" + limite + "&api_key=" + chavecryptocompare;
        
        //mierclasses.mcfuncoeshelper.setarclipboard(caminhourl);
        //mierclasses.mcfuncoeshelper.mostrarmensagem(caminhourl);
        
        String jsonconteudo = mwcomms.receberconteudopagina(caminhourl);
        //mierclasses.mcfuncoeshelper.mostrarmensagem(jsonconteudo);
        mierclasses.mcfuncoeshelper.setarclipboard(jsonconteudo);
        JSONObject obj = new JSONObject(jsonconteudo);
        
        JSONArray datacandles = obj.getJSONArray("Data");
        java.util.List<mccandle> listacandlesretornar = new java.util.ArrayList<>();
        for (int i = 0; i < datacandles.length(); i++)
        {
            JSONObject candleobjectatual = datacandles.getJSONObject(i);
            String cjson_date = String.valueOf(candleobjectatual.getLong("time"));
            String cjson_open = String.valueOf(candleobjectatual.getDouble("open"));
            String cjson_high = String.valueOf(candleobjectatual.getDouble("high"));
            String cjson_close = String.valueOf(candleobjectatual.getDouble("close"));
            String cjson_low = String.valueOf(candleobjectatual.getDouble("low"));
            String cjson_volume = String.valueOf(candleobjectatual.getDouble("volumefrom"));
            
            
            java.util.Date timestampdate = new java.util.Date(Long.parseLong(cjson_date) * 1000);
            //mierclasses.mcfuncoeshelper.mostrarmensagem(timestampdate.toString());
            
            String timestampcandle = 
                    (timestampdate.getYear()+1900) + "-" +
                    (timestampdate.getMonth()+1) + "-" +
                    (timestampdate.getDate()) + "-" +
                    (timestampdate.getHours()) + "-" +
                    (timestampdate.getMinutes()) + "-" +
                    (timestampdate.getSeconds()); 
            

            
            mierclasses.mccandle candleadd = new mierclasses.mccandle
        (timestampcandle, cjson_open, cjson_high, cjson_close, cjson_low, cjson_volume);
            
            listacandlesretornar.add(candleadd);
            
        }
        
        return listacandlesretornar;
        
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="IEX API">
    
    public void testecomunicacaoiex()
    {
        String conteudopagina = mwcomms.receberconteudopagina("https://api.iextrading.com/1.0/stock/aapl/chart/5y");
        mierclasses.mcfuncoeshelper.mostrarmensagem(conteudopagina);
    }
    
    public java.util.List<String> receberlistasimbolosiexprocura(String parametrobusca)
    {
        //funcao para retornar uma lista de simbolos encontrados para o parametro de busca em questao
        
        /*
        
        [
            {
                "symbol": "A",
                "name": "Agilent Technologies Inc.",
                "date": "2019-01-04",
                "isEnabled": true,
                "type": "cs",
                "iexId": "2"
            },
            {
                "symbol": "AA",
                "name": "Alcoa Corporation",
                "date": "2019-01-04",
                "isEnabled": true,
                "type": "cs",
                "iexId": "12042"
            },
            {
                "symbol": "AAAU",
                "name": "Perth Mint Physical Gold",
                "date": "2019-01-04",
                "isEnabled": true,
                "type": "N/A",
                "iexId": "14924"
            },
            {
                "symbol": "AABA",
                "name": "Altaba Inc.",
                "date": "2019-01-04",
                "isEnabled": true,
                "type": "cs",
                "iexId": "7653"
            },
            {
                "symbol": "AAC",
                "name": "AAC Holdings Inc.",
                "date": "2019-01-04",
                "isEnabled": true,
                "type": "cs",
                "iexId": "9169"
            },
            {
                "symbol": "AADR",
                "name": "AdvisorShares Dorsey Wright ADR",
                "date": "2019-01-04",
                "isEnabled": true,
                "type": "et",
                "iexId": "5"
            },
            {
                "symbol": "AAL",
                "name": "American Airlines Group Inc.",
                "date": "2019-01-04",
                "isEnabled": true,
                "type": "cs",
                "iexId": "8148"
            },
            {
                "symbol": "AAMC",
                "name": "Altisource Asset Management Corp Com",
                "date": "2019-01-04",
                "isEnabled": true,
                "type": "cs",
                "iexId": "7760"
            },
            {
                "symbol": "AAME",
                "name": "Atlantic American Corporation",
                "date": "2019-01-04",
                "isEnabled": true,
                "type": "cs",
                "iexId": "7"
            },
            {
                "symbol": "AAN",
                "name": "Aaron's Inc.",
                "date": "2019-01-04",
                "isEnabled": true,
                "type": "cs",
                "iexId": "8"
            },
            {
                "symbol": "AAOI",
                "name": "Applied Optoelectronics Inc.",
                "date": "2019-01-04",
                "isEnabled": true,
                "type": "cs",
                "iexId": "7790"
            },
            {
                "symbol": "AAON",
                "name": "AAON Inc.",
                "date": "2019-01-04",
                "isEnabled": true,
                "type": "cs",
                "iexId": "9"
            },
            {
                "symbol": "AAP",
                "name": "Advance Auto Parts Inc W/I",
                "date": "2019-01-04",
                "isEnabled": true,
                "type": "cs",
                "iexId": "10"
            },
            {
                "symbol": "AAPL",
                "name": "Apple Inc.",
                "date": "2019-01-04",
                "isEnabled": true,
                "type": "cs",
                "iexId": "11"
            },
            {
                "symbol": "AAT",
                "name": "American Assets Trust Inc.",
                "date": "2019-01-04",
                "isEnabled": true,
                "type": "cs",
                "iexId": "12"
            },
            {
                "symbol": "AAU",
                "name": "Almaden Minerals Ltd.",
                "date": "2019-01-04",
                "isEnabled": true,
                "type": "cs",
                "iexId": "13"
            }
        ]
        
        */
        
        String jsonconteudo = mwcomms.receberconteudopagina("https://api.iextrading.com/1.0/ref-data/symbols");
        
        JSONArray simbolosjson = new JSONArray(jsonconteudo);
        java.util.List<String> listasimbolosencontrada = new java.util.ArrayList<>();
        for (int i = 0; i < simbolosjson.length(); i++)
        {
            JSONObject candleobjectatual = simbolosjson.getJSONObject(i);
            
            String s_symbol = candleobjectatual.getString("symbol");
            String s_name = candleobjectatual.getString("name");
            //String s_date = candleobjectatual.getString("date");
            //String s_isenabled = String.valueOf(candleobjectatual.getBoolean("isEnabled"));
            String s_type = candleobjectatual.getString("type");
            //String s_iedid = candleobjectatual.getString("iexId");
            
            if ((s_symbol.toLowerCase()).contains(parametrobusca.toLowerCase()))
            {
                String simboloadd = "IEX:" + s_symbol + " - " + s_name + " (" + s_type + ")";
                listasimbolosencontrada.add(simboloadd);
            }
            else if ((s_name.toLowerCase()).contains(parametrobusca.toLowerCase()))
            {
                String simboloadd = "IEX:" + s_symbol + " - " + s_name + " (" + s_type + ")";
                listasimbolosencontrada.add(simboloadd);
            }
            
        }
        
        return listasimbolosencontrada;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Stocks - Chart">
    
    public java.util.List<mccandle> receberstockchartwithoutminutes(String simbolo, String periodo)
    {
        /*
        [
            {
                "date": "2014-01-06",
                "open": 70.4653,
                "high": 71.6912,
                "low": 69.9605,
                "close": 71.3149,
                "volume": 103359151,
                "unadjustedVolume": 14765593,
                "change": 0.386777,
                "changePercent": 0.545,
                "vwap": 70.9303,
                "label": "Jan 6, 14",
                "changeOverTime": 0
            },
            {
                "date": "2014-01-07",
                "open": 71.366,
                "high": 71.5811,
                "low": 70.5276,
                "close": 70.8046,
                "volume": 79432766,
                "unadjustedVolume": 11347538,
                "change": -0.510346,
                "changePercent": -0.716,
                "vwap": 71.7785,
                "label": "Jan 7, 14",
                "changeOverTime": -0.007155587401791223
            },
            {
                "date": "2014-01-08",
                "open": 70.6436,
                "high": 71.5286,
                "low": 70.6279,
                "close": 71.2533,
                "volume": 64686685,
                "unadjustedVolume": 9240955,
                "change": 0.448723,
                "changePercent": 0.634,
                "vwap": 71.2531,
                "label": "Jan 8, 14",
                "changeOverTime": -0.000863774610915791
            },
            {
                "date": "2014-01-09",
                "open": 71.6912,
                "high": 71.6991,
                "low": 70.19,
                "close": 70.3432,
                "volume": 69905199,
                "unadjustedVolume": 9986457,
                "change": -0.910039,
                "changePercent": -1.277,
                "vwap": 70.8314,
                "label": "Jan 9, 14",
                "changeOverTime": -0.013625483594592414
            }
        ]
        
        
        */
        
        String jsonconteudo = mwcomms.receberconteudopagina("https://api.iextrading.com/1.0/stock/"+simbolo+"/chart/"+periodo);
        
        JSONArray candlesjson = new JSONArray(jsonconteudo);
        java.util.List<mccandle> listacandlesretornar = new java.util.ArrayList<>();
        for (int i = 0; i < candlesjson.length(); i++)
        {
            JSONObject candleobjectatual = candlesjson.getJSONObject(i);
            String cjson_date = candleobjectatual.getString("date");
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cjson_date);
            String cjson_open = String.valueOf(candleobjectatual.getDouble("open"));
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cjson_open);
            String cjson_high = String.valueOf(candleobjectatual.getDouble("high"));
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cjson_high);
            String cjson_close = String.valueOf(candleobjectatual.getDouble("close"));
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cjson_close);
            String cjson_low = String.valueOf(candleobjectatual.getDouble("low"));
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cjson_low);
            String cjson_volume = String.valueOf(candleobjectatual.getDouble("volume"));
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cjson_volume);
            
            String timestampcandle = 
                    cjson_date.split("-")[0] + "-" +
                    cjson_date.split("-")[1] + "-" +
                    cjson_date.split("-")[2] + "-" +
                    "00" + "-" +
                    "00" + "-" +
                    "00"; 
            
            mierclasses.mccandle candleadd = new mierclasses.mccandle
        (timestampcandle, cjson_open, cjson_high, cjson_close, cjson_low, cjson_volume);
            
            listacandlesretornar.add(candleadd);
            
        }
        
        return listacandlesretornar;
    }
    
    public java.util.List<mccandle> receberstockchartwithminutes(String simbolo, String periodo)
    {
        /*
        [
            {
                "date": "20190104",
                "minute": "09:30",
                "label": "09:30 AM",
                "high": 144.54,
                "low": 144.07,
                "average": 144.356,
                "volume": 9329,
                "notional": 1346694.07,
                "numberOfTrades": 95,
                "marketHigh": 144.58,
                "marketLow": 144.06,
                "marketAverage": 144.451,
                "marketVolume": 2029196,
                "marketNotional": 293119046.0123,
                "marketNumberOfTrades": 3799,
                "open": 144.42,
                "close": 144.48,
                "marketOpen": 144.58,
                "marketClose": 144.51,
                "changeOverTime": 0,
                "marketChangeOverTime": 0
            },
            {
                "date": "20190104",
                "minute": "09:31",
                "label": "09:31 AM",
                "high": 144.49,
                "low": 143.82,
                "average": 144.162,
                "volume": 14569,
                "notional": 2100302.925,
                "numberOfTrades": 79,
                "marketHigh": 144.54,
                "marketLow": 143.6,
                "marketAverage": 144.035,
                "marketVolume": 472509,
                "marketNotional": 68058023.4464,
                "marketNumberOfTrades": 3741,
                "open": 144.49,
                "close": 143.91,
                "marketOpen": 144.509,
                "marketClose": 143.95,
                "changeOverTime": -0.0013438998032640722,
                "marketChangeOverTime": -0.0028798692982395196
            }
        ]
        
        */
        
        String jsonconteudo = mwcomms.receberconteudopagina("https://api.iextrading.com/1.0/stock/"+simbolo+"/chart/"+periodo);
        //mierclasses.mcfuncoeshelper.setarclipboard(jsonconteudo);
        //mierclasses.mcfuncoeshelper.mostrarmensagem(jsonconteudo);
        
        JSONArray candlesjson = new JSONArray(jsonconteudo);
        java.util.List<mccandle> listacandlesretornar = new java.util.ArrayList<>();
        for (int i = 0; i < candlesjson.length(); i++)
        {
            JSONObject candleobjectatual = candlesjson.getJSONObject(i);
            String cjson_date = candleobjectatual.getString("date");
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cjson_date);
            String cjson_minute = candleobjectatual.getString("minute");
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cjson_minute);
            String cjson_open = String.valueOf(candleobjectatual.getDouble("open"));
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cjson_open);
            String cjson_high = String.valueOf(candleobjectatual.getDouble("high"));
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cjson_high);
            String cjson_close = String.valueOf(candleobjectatual.getDouble("close"));
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cjson_close);
            String cjson_low = String.valueOf(candleobjectatual.getDouble("low"));
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cjson_low);
            String cjson_volume = String.valueOf(candleobjectatual.getDouble("volume"));
            //mierclasses.mcfuncoeshelper.mostrarmensagem(cjson_volume);
            
            String anotimestamp = mierclasses.mcfuncoeshelper.retornarSubstringIndices(cjson_date, 0, 3);
            //mierclasses.mcfuncoeshelper.mostrarmensagem(anotimestamp);
            String mestimestamp = mierclasses.mcfuncoeshelper.retornarSubstringIndices(cjson_date, 4, 5);
            //mierclasses.mcfuncoeshelper.mostrarmensagem(mestimestamp);
            String diatimestamp = mierclasses.mcfuncoeshelper.retornarSubstringIndices(cjson_date, 6, 7);
            //mierclasses.mcfuncoeshelper.mostrarmensagem(diatimestamp);
            String horatimestamp = cjson_minute.split(":")[0];
            //mierclasses.mcfuncoeshelper.mostrarmensagem(horatimestamp);
            String minutotimestamp = cjson_minute.split(":")[1];
            //mierclasses.mcfuncoeshelper.mostrarmensagem(minutotimestamp);
            
            String timestampcandle = 
                    anotimestamp + "-" +
                    mestimestamp + "-" +
                    diatimestamp + "-" +
                    horatimestamp + "-" +
                    minutotimestamp + "-" +
                    "00"; 
            
            mierclasses.mccandle candleadd = new mierclasses.mccandle
        (timestampcandle, cjson_open, cjson_high, cjson_close, cjson_low, cjson_volume);
            
            listacandlesretornar.add(candleadd);
            
        }
        
        return listacandlesretornar;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Stocks - Quote">
    public mcquote receberquote(String simbolo)
    {
        //funcao para receber informacoes de quote de um simbolo especifico
        String jsonconteudo = mwcomms.receberconteudopagina("https://api.iextrading.com/1.0/stock/"+simbolo+"/quote");

        
        JSONObject quoteobject = new JSONObject(jsonconteudo);
        String qjson_symbol = quoteobject.getString("symbol");
        String qjson_cname = quoteobject.getString("companyName");
        String qjson_open = String.valueOf(quoteobject.getDouble("open"));
        String qjson_opentime = String.valueOf(quoteobject.getLong("openTime"));
        String qjson_close = String.valueOf(quoteobject.getDouble("close"));
        String qjson_closetime = String.valueOf(quoteobject.getLong("closeTime"));
        String qjson_high = String.valueOf(quoteobject.getDouble("high"));
        String qjson_low = String.valueOf(quoteobject.getDouble("low"));
        String qjson_lvolume = String.valueOf(quoteobject.getLong("latestVolume"));
        String qjon_pclose = String.valueOf(quoteobject.getDouble("previousClose"));
        
        mierclasses.mcquote mcquoteretornar = new mierclasses.mcquote
            (
                qjson_symbol, qjson_cname, qjson_open, 
                qjson_opentime, qjson_close, qjson_closetime, 
                qjson_high, qjson_low, qjson_lvolume, qjon_pclose
            );
        
        
        return mcquoteretornar;
    }
    //</editor-fold>
    
    // </editor-fold> 
    
    // <editor-fold defaultstate="collapsed" desc="OFFLINE VALUES">
    
    //the values returned by these functions are not real and used for offline trading and testing only 
    public java.util.List<mccandle> receberstockchartsample()
    {
        //funcao para entregar lista de cadles de exemplo
        String rootjar = mierclasses.mcfuncoeshelper.retornarpathbaseprograma();
        String samplejson = rootjar + "/outfiles/samples/apple5y.json";
        String jsonconteudo = mierclasses.mcfuncoeshelper.retornarStringArquivo(samplejson);
        
        JSONArray candlesjson = new JSONArray(jsonconteudo);
        java.util.List<mccandle> listacandlesretornar = new java.util.ArrayList<>();
        for (int i = 0; i < candlesjson.length(); i++)
        {
            JSONObject candleobjectatual = candlesjson.getJSONObject(i);
            String cjson_date = candleobjectatual.getString("date");
            String cjson_open = String.valueOf(candleobjectatual.getDouble("open"));
            String cjson_high = String.valueOf(candleobjectatual.getDouble("high"));
            String cjson_close = String.valueOf(candleobjectatual.getDouble("close"));
            String cjson_low = String.valueOf(candleobjectatual.getDouble("low"));
            String cjson_volume = String.valueOf(candleobjectatual.getDouble("volume"));
            
            String timestampcandle = 
                    cjson_date.split("-")[0] + "-" +
                    cjson_date.split("-")[1] + "-" +
                    cjson_date.split("-")[2] + "-" +
                    "00" + "-" +
                    "00" + "-" +
                    "00"; 
            
            mierclasses.mccandle candleadd = new mierclasses.mccandle
        (timestampcandle, cjson_open, cjson_high, cjson_close, cjson_low, cjson_volume);
            
            listacandlesretornar.add(candleadd);
            
        }
        
        return listacandlesretornar;
    }
    
    public java.util.List<Double> receberlastbidaskofflinetrading(java.util.List<mierclasses.mccandle> candlesatual)
    {
        //funcao para estimar um valor de ask e bid para o simbolo atual, para testar o trader bot
        //mierclasses.mcquote quote = receberquote(simbolo);
        //double ultimoclose = quote.closed;
        //double rangebidaskestimado = 0.0025;
        java.util.List<mierclasses.mccandle> candlessample = candlesatual;
        mierclasses.mccandle ultimacandlesample = candlessample.get(candlessample.size()-1);
        double ultimoclose = ultimacandlesample.closed;
        double rangebidaskestimado = 0.0025;
        
        double variacao = ultimoclose*rangebidaskestimado;
        double bidestimado = ultimoclose - variacao;
        double askestimado = ultimoclose + variacao;

        //https://stackoverflow.com/questions/20039098/issue-creating-a-double-array-list
        java.util.List<Double> retorno = new java.util.ArrayList<>();
        retorno.add(bidestimado);
        retorno.add(askestimado);
        
        return retorno;
    }
    
    public java.util.List<Double> receberlastbidaskofflinetradingsample()
    {
        //funcao para receber o ultimo ask bid para o sample
        java.util.List<mierclasses.mccandle> candlessample = receberstockchartsample();
        mierclasses.mccandle ultimacandlesample = candlessample.get(candlessample.size()-1);
        
        double ultimoclose = ultimacandlesample.closed;
        
        double rangebidaskestimado = 0.0025;
        
        //os valores de bid e ask no momento serao estimados, ja que os valores de ask e bid reais nem sempre estao
        //disponiveis pelo IEX API por conta de diversos fatores: indisponibilidade de servico, market closed, etc,
        //e o interessante no modo offline eh sempre ter um valor para testar o bot em cima.
        
        //no momento o bid e ask serao estimados como -+ 0.25% do close final recebido,
        //e eles nunca sairao do high low da candle
        
        double variacao = ultimoclose*rangebidaskestimado;
        double bidestimado = ultimoclose - variacao;
        double askestimado = ultimoclose + variacao;
        
        //https://stackoverflow.com/questions/20039098/issue-creating-a-double-array-list
        java.util.List<Double> retorno = new java.util.ArrayList<>();
        retorno.add(bidestimado);
        retorno.add(askestimado);
        
        return retorno;
    }
    // </editor-fold>
}
