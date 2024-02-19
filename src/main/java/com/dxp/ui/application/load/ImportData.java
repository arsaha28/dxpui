package com.dxp.ui.application.load;

import com.dxp.ui.application.model.TransactionML;

import java.text.SimpleDateFormat;
import java.util.*;

public class ImportData {

    static Map<String,String> map = new HashMap<>();
    static Map<String,String> mapMCC = new HashMap<>();
    static {
            map.put("Amazon","5732");
            map.put("Tesco","5411");
            map.put("MarksSpencer","5999");
            map.put("Argos","5732");
            map.put("Sainsburys","5411");
            map.put("ASDA","5411");
            map.put("JohnLewis","5999");
            map.put("Boots","5912");
            map.put("CurrysPCWorld","5732");
            map.put("BANDQ","5211");
            map.put("Next","5691");
            map.put("Superdrug","5912");
            map.put("Morrisons","5411");
            map.put("Lidl","5411");
            map.put("Aldi","5411");
            map.put("Homebase","5211");
            map.put("Very","5999");
            map.put("AO.com","5732");
            map.put("SportsDirect ","5641   ");
            map.put("Wilko","5999");
            map.put("GAME","5732");
            map.put("Habitat","5712");
            map.put("WHSmith","5999");
            map.put("TheRange","5999");
            map.put("BMBargains","5999");
            map.put("Screwfix","5261");
            map.put("Iceland","5411");
            map.put("Poundland","5999");
            map.put("RobertDyas","5261");
    }

    static {
            mapMCC.put("5732",	"Electronics Stores");
            mapMCC.put("5411",	"Grocery Stores Supermarkets");
            mapMCC.put("5999",	"Miscellaneous Retail Stores");
            mapMCC.put("5732",	"Electronics Stores");
            mapMCC.put("5411",	"Grocery Stores Supermarkets");
            mapMCC.put("5411",	"Grocery Stores Supermarkets");
            mapMCC.put("5999",	"Miscellaneous Retail Stores");
            mapMCC.put("5912",	"Drugstores and Pharmacies");
            mapMCC.put("5732",	"Electronics Stores");
            mapMCC.put("5211",	"Lumberyards and Building Materials Stores");
            mapMCC.put("5691",	"Men's Clothing Stores");
            mapMCC.put("5912",	"Drugstores and Pharmacies");
            mapMCC.put("5411",	"Grocery Stores Supermarkets");
            mapMCC.put("5411",	"Grocery Stores Supermarkets");
            mapMCC.put("5411",	"Grocery Stores Supermarkets");
            mapMCC.put("5211",	"Lumberyards and Building Materials Stores");
            mapMCC.put("5999",	"Miscellaneous Retail Stores");
            mapMCC.put("5732",	"Electronics Stores");
            mapMCC.put("5999",	"Miscellaneous Retail Stores");
            mapMCC.put("5641",	"Sporting Goods Stores");
            mapMCC.put("5999",	"Miscellaneous Retail Stores");
            mapMCC.put("5732",	"Electronics Stores");
            mapMCC.put("5712",	"Furniture Stores");
            mapMCC.put("5999",	"Miscellaneous Retail Stores");
            mapMCC.put("5999",	"Miscellaneous Retail Stores");
            mapMCC.put("5999",	"Miscellaneous Retail Stores");
            mapMCC.put("5261",	"Paint and Hardware Stores");
            mapMCC.put("5411",	"Grocery Stores Supermarkets");
            mapMCC.put("5999",	"Miscellaneous Retail Stores");
            mapMCC.put("5261",	"Paint and Hardware Stores");
    }
    static List<String> places = Arrays.asList("London",
            "Edinburgh",
            "Manchester",
            "Birmingham",
            "Liverpool",
            "Glasgow",
            "Belfast",
            "Oxford",
            "Cambridge",
            "Bristol",
            "York",
            "Cardiff",
            "Brighton",
            "Bath",
            "Leeds",
            "Newcastle upon Tyne",
            "Sheffield",
            "Nottingham",
            "Stratford-upon-Avon",
            "Canterbury",
            "Inverness",
            "St. Andrews",
            "Aberdeen",
            "Southampton",
            "Dundee",
            "Plymouth",
            "Windsor",
            "Chester",
            "Swansea",
            "Portsmouth");

    public static List<TransactionML> loadData(int limit) throws CloneNotSupportedException {
        List<TransactionML> trxList  = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        for(int i = 0;i<limit;++i){
            String merchant = randomMerchant();
            TransactionML transaction = new TransactionML(randomAmount(),merchant,map.get(merchant),randomPlace(),null);
            transaction.setDate(formatter.format(new Date()));
            trxList.add(transaction);
        }
        TransactionML recurring = (TransactionML) trxList.get(0).clone();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH,1);
        recurring.setDate(formatter.format(calendar.getTime()));
        trxList.add(recurring);
        return trxList;
    }

    private static String randomPlace(){
        int index = (int)(Math.random() * places.size());
        return places.get(index);

    }

    private static String randomMerchant(){
        List<String> keys = new ArrayList<String>(map.keySet());
        int keyIndex = new Random().nextInt(keys.size());
        return keys.get(keyIndex);
    }

    private static String randomAmount(){
        int start = 2;
        int end = 30;
        Random rn = new Random();
        int range = end - start + 1;
        Integer randomNum =  rn.nextInt(range) + start;
        return randomNum.toString();
    }
}
