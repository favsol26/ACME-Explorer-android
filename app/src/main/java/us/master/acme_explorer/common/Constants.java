package us.master.acme_explorer.common;

public final class Constants {

    public final static String controlFilter = "controlFilter", filterPreferences = "Filters",
            dateStart = "dateStart", dateEnd = "dateEnd", maxPrice = "maxPrice",
            minPrice = "minPrice", IntentTrip = "Travel", newEmail = "newEmail", from = "from",
            longitude = "longitude", latitude = "latitude", maps = "maps",
            regExPassword = "^" +
                    "(?=.*[0-9])" +
                    "(?=.*[A-Z])" +
                    "(?=.*[@#$%^&+=!])" +
                    "(?=\\S+$)" +
                    ".{6,}" +
                    "$";

    public final static String[] urlImages = {
            "https://static.wixstatic.com/media/a6b425_48880d306dd5487ab1f9fed9a4ab7f91~mv2.jpg",
            "https://www.lavanguardia.com/r/GODO/LV/p5/WebSite/2018/06/15/Recortada/img_lberna" +
                    "us_20180615-100456_imageNes_lv_terCEros_istOck-492416114-109-".toLowerCase() +
                    "kn3E-U45119239404E0-992x558@LaVanguardia-Web.jpg"
    };
}