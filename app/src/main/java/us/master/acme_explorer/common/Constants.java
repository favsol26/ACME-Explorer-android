package us.master.acme_explorer.common;

import java.util.Hashtable;

public final class Constants {

    public final static String[] cities = {
            "Tirana-Albania", "Berlín-Germany", "Andorra La Vieja-Andorra", "Ereván-Armenia",
            "Viena-Austria", "Bakú-Azerbaijan", "Bruselas-Belgium", "Minsk-Belarus",
            "Sarajevo-Bosnia and Herzegovina", "Sofía-Bulgaria", "Praga-Czech Republic",
            "Zagreb-Croatia", "Copenhague-Denmark", "Bratislava-Slovakia", "New York-US",
            "Lublijana-Slovenia", "Madrid-Spain", "Tallín-Estonia", "Helsinki-Finland",
            "París-France", "Tiflis-Georgia", "Atenas-Greece", "Budapest-Hungary", "Kiev-Ukraine",
            "Reikiavik-Iceland", "Washington-US", "Roma-Italy", "Estocolmo-Sweden",
            "Oslo-Norway", "Londres-United Kingdom", "San Juan De La Maguana-Rep. Dom.",
            "Santo Domingo-Rep. Dom.", "Concepción De La Vega-Rep. Dom.", "Dublín-Ireland",
            "Santiago De Los Caballeros-Rep. Dom.", "Segovia-Spain", "Málaga-Spain",
            "Ciudad Del Vaticano-Vatican City", "Luxemburgo-Luxembourg", "Mónaco-Monaco",
            "Podgorica-Montenegro", "Ciudad De San Marino-San Marino", "Lisboa-Portugal",
            "Chisinau-Moldova", "Moscú-Russia", "Varsovia-Poland", "Berna-Switzerland",
            "Belgrado-Serbia", "La Valeta-Malta", "Amsterdam-Netherlands", "Bucarest-Romania",
            "Riga-Latvia", "Vaduz-Liechtenstein", "Vilna-Lithuania", "Skopje-North Macedonia"
    };

    public final static String controlFilter = "controlFilter", filterPreferences = "Filters",
            dateStart = "dateStart", dateEnd = "dateEnd", maxPrice = "maxPrice",
            minPrice = "minPrice", IntentTravel = "Travel";

    public final static String[] departurePlace = {"Sevilla", "Punta Cana",
            "Cádiz", "Zaragoza", "Barcelona", "Granada", "Valencia", "Oporto", "Huelva"};

    public final static String[] urlImages = {
            "https://static.wixstatic.com/media/a6b425_48880d306dd5487ab1f9fed9a4ab7f91~mv2.jpg",
            "https://www.lavanguardia.com/r/GODO/LV/p5/WebSite/2018/06/15/Recortada/img_lberna" +
                    "us_20180615-100456_imageNes_lv_terCEros_istOck-492416114-109-".toLowerCase() +
                    "kn3E-U45119239404E0-992x558@LaVanguardia-Web.jpg"
    };

    public static final Hashtable<String, String> citiesToCountry = new Hashtable<String, String>() {
        {
            for (String item : cities) {
                put(item.substring(0, item.indexOf("-")), item.substring(item.indexOf("-") + 1));
            }
        }
    };

    public static final Hashtable<String, String> flagsLinks = new Hashtable<String, String>() {
        {
            String baseLink = "https://upload.wikimedia.org/wikipedia/commons/thumb";
            String baseLink2 = "https://upload.wikimedia.org/wikipedia";

            put("Lithuania", baseLink.concat("/1/11/Flag_of_Lithuania.svg/1024px-Flag_of_Lithuania.svg.png"));
            put("North Macedonia", baseLink.concat("/7/79/Flag_of_North_Macedonia.svg/1280px-Flag_of_North_Macedonia.svg.png"));
            put("Liechtenstein", baseLink.concat("/4/47/Flag_of_Liechtenstein.svg/1024px-Flag_of_Liechtenstein.svg.png"));
            put("Latvia", baseLink.concat("/8/84/Flag_of_Latvia.svg/1280px-Flag_of_Latvia.svg.png"));
            put("Malta", baseLink.concat("/7/73/Flag_of_Malta.svg/1024px-Flag_of_Malta.svg.png"));
            put("Netherlands", baseLink.concat("/2/20/Flag_of_the_Netherlands.svg/1024px-Flag_of_the_Netherlands.svg.png"));
            put("Romania", baseLink.concat("/7/73/Flag_of_Romania.svg/1024px-Flag_of_Romania.svg.png"));
            put("Serbia", baseLink.concat("/f/ff/Flag_of_Serbia.svg/800px-Flag_of_Serbia.svg.png"));
            put("Montenegro", baseLink.concat("/6/64/Flag_of_Montenegro.svg/1280px-Flag_of_Montenegro.svg.png"));
            put("Monaco", baseLink.concat("/e/ea/Flag_of_Monaco.svg/800px-Flag_of_Monaco.svg.png"));
            put("San Marino", baseLink.concat("/b/b1/Flag_of_San_Marino.svg/800px-Flag_of_San_Marino.svg.png"));
            put("Poland", baseLink.concat("/1/12/Flag_of_Poland.svg/1024px-Flag_of_Poland.svg.png"));
            put("Denmark", baseLink.concat("/9/9c/Flag_of_Denmark.svg/800px-Flag_of_Denmark.svg.png"));
            put("Sweden", baseLink.concat("/4/4c/Flag_of_Sweden.svg/1024px-Flag_of_Sweden.svg.png"));
            put("Moldova", baseLink.concat("/2/27/Flag_of_Moldova.svg/1280px-Flag_of_Moldova.svg.png"));
            put("Ukraine", baseLink.concat("/4/49/Flag_of_Ukraine.svg/800px-Flag_of_Ukraine.svg.png"));
            put("Russia", baseLink.concat("/f/f3/Flag_of_Russia.svg/1024px-Flag_of_Russia.svg.png"));
            put("Luxembourg", baseLink.concat("/d/da/Flag_of_Luxembourg.svg/1024px-Flag_of_Luxembourg.svg.png"));
            put("Iceland", baseLink.concat("/c/ce/Flag_of_Iceland.svg/800px-Flag_of_Iceland.svg.png"));
            put("Ireland", baseLink.concat("/4/45/Flag_of_Ireland.svg/1280px-Flag_of_Ireland.svg.png"));
            put("Hungary", baseLink.concat("/c/c1/Flag_of_Hungary.svg/800px-Flag_of_Hungary.svg.png"));
            put("Greece", baseLink.concat("/5/5c/Flag_of_Greece.svg/600px-Flag_of_Greece.svg.png"));
            put("Georgia", baseLink.concat("/0/0f/Flag_of_Georgia.svg/1024px-Flag_of_Georgia.svg.png"));
            put("Italy", baseLink.concat("/0/03/Flag_of_Italy.svg/1024px-Flag_of_Italy.svg.png"));
            put("Slovenia", baseLink.concat("/f/f0/Flag_of_Slovenia.svg/1280px-Flag_of_Slovenia.svg.png"));
            put("Estonia", baseLink.concat("/8/8f/Flag_of_Estonia.svg/1024px-Flag_of_Estonia.svg.png"));
            put("Finland", baseLink.concat("/b/bc/Flag_of_Finland.svg/1024px-Flag_of_Finland.svg.png"));
            put("Spain", baseLink2.concat("/en/thumb/9/9a/Flag_of_Spain.svg/1200px-Flag_of_Spain.svg.png"));
            put("Norway", baseLink.concat("/d/d9/Flag_of_Norway.svg/800px-Flag_of_Norway.svg.png"));
            put("Albania", baseLink.concat("/3/36/Flag_of_Albania.svg/800px-Flag_of_Albania.svg.png"));
            put("Germany", baseLink.concat("/b/ba/Flag_of_Germany.svg/1024px-Flag_of_Germany.svg.png"));
            put("Andorra", baseLink.concat("/1/19/Flag_of_Andorra.svg/800px-Flag_of_Andorra.svg.png"));
            put("Armenia", baseLink.concat("/2/2f/Flag_of_Armenia.svg/1280px-Flag_of_Armenia.svg.png"));
            put("Portugal", baseLink.concat("/5/5c/Flag_of_Portugal.svg/1024px-Flag_of_Portugal.svg.png"));
            put("Belarus", baseLink.concat("/8/85/Flag_of_Belarus.svg/1280px-Flag_of_Belarus.svg.png"));
            put("Belgium", baseLink.concat("/6/65/Flag_of_Belgium.svg/800px-Flag_of_Belgium.svg.png"));
            put("Croatia", baseLink.concat("/1/1b/Flag_of_Croatia.svg/1280px-Flag_of_Croatia.svg.png"));
            put("Austria", baseLink.concat("/4/41/Flag_of_Austria.svg/1024px-Flag_of_Austria.svg.png"));
            put("Azerbaijan", baseLink.concat("/d/dd/Flag_of_Azerbaijan.svg/1280px-Flag_of_Azerbaijan.svg.png"));
            put("Bulgaria", baseLink.concat("/9/9a/Flag_of_Bulgaria.svg/1024px-Flag_of_Bulgaria.svg.png"));
            put("France", baseLink.concat("/c/c3/Flag_of_France.svg/1024px-Flag_of_France.svg.png"));
            put("Slovakia", baseLink.concat("/e/e6/Flag_of_Slovakia.svg/1024px-Flag_of_Slovakia.svg.png"));
            put("Vatican City", baseLink.concat("/0/00/Flag_of_the_Vatican_City.svg/800px-" +
                    "Flag_of_the_Vatican_City.svg.png"));
            put("Switzerland", baseLink.concat("/0/08/Flag_of_Switzerland_%28Pantone%29.svg/320px-" +
                    "Flag_of_Switzerland_%28Pantone%29.svg.png"));
            put("United Kingdom", baseLink.concat("/a/ae/Flag_of_the_United_Kingdom.svg/1280px-" +
                    "Flag_of_the_United_Kingdom.svg.png"));
            put("US", baseLink.concat("/a/a4/Flag_of_the_United_States.svg/1280px-" +
                    "Flag_of_the_United_States.svg.png"));
            put("Bosnia and Herzegovina", baseLink.concat("/b/bf/Flag_of_Bosnia_and_Herzegovina.svg/1280px-" +
                    "Flag_of_Bosnia_and_Herzegovina.svg.png"));
            put("Czech Republic", baseLink.concat("/c/cb/Flag_of_the_Czech_Republic.svg/1024px-" +
                    "Flag_of_the_Czech_Republic.svg.png"));
            put("Rep. Dom.", baseLink.concat("/9/9f/Flag_of_the_Dominican_Republic.svg/1024px-" +
                    "Flag_of_the_Dominican_Republic.svg.png"));
        }
    };
}