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
public class mciexcomms
{
    //classe utilizada para comunicacao web
    mcwebcomms mwcomms;
    
    public mciexcomms()
    {
        mwcomms = new mcwebcomms();
    }
    
    public void testecomunicacaoiex()
    {
        String conteudopagina = mwcomms.receberconteudopagina("https://api.iextrading.com/1.0/stock/aapl/chart/5y");
        mierclasses.mcfuncoeshelper.mostrarmensagem(conteudopagina);
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
    
    // <editor-fold defaultstate="collapsed" desc="Stocks - Chart">
    
    public java.util.List<String> receberlistasimbolosprocura(String parametrobusca)
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
                String simboloadd = s_symbol + " - " + s_name + " (" + s_type + ")";
                listasimbolosencontrada.add(simboloadd);
            }
            else if ((s_name.toLowerCase()).contains(parametrobusca.toLowerCase()))
            {
                String simboloadd = s_symbol + " - " + s_name + " (" + s_type + ")";
                listasimbolosencontrada.add(simboloadd);
            }
            
        }
        
        return listasimbolosencontrada;
    }
    
    // </editor-fold>
}
