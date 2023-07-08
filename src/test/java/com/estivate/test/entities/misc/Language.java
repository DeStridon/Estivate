package com.estivate.test.entities.misc;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;



public enum Language implements Serializable{
	
	//See https://msdn.microsoft.com/en-us/library/ee825488(v=cs.20).aspx for reference

	// America
	en_US("English", "United States"),
	pt_BR("Portuguese", "Brazil"),
	en_CA("English","Canada"),
	es_AR("Spanish", "Argentina"),
	es_MX("Spanish", "Mexico"),
	es_CL("Spanish", "Chili"),
	es_CO("Spanish", "Columbia"),
	es_UY("Spanish", "Uruguay"),
	es_PE("Spanish", "Peru"),
	es_PA("Spanish", "Panama"),
	es_PY("Spanish", "Paraguay"),
	es_US("Spanish", "United States"),
	es_VE("Spanish", "Venezuela"),
	es_BO("Spanish", "Bolivia"),
	es_DO("Spanish", "Dominican Republic"),
	es_EC("Spanish", "Ecuador"),
	es_GT("Spanish", "Guatemala"),
	es_CR("Spanish", "Costa Rica"),
	es_HN("Spanish", "Honduras"),
	es_NI("Spanish", "Nicaragua"),
	es_LX("Spanish", "Latin America"),
	
	
	// Europe
	az_AZ("Azerbaijani", "Azerbaijan"),
	bg_BG("Bulgarian", "Bulgary"),
	ca_ES("Catalan", "Spain"),
	cs_CZ("Czech", "Czechia"),
	da_DK("Danish", "Denmark"),
	de_AT("German", "Austria"),
	de_CH("German", "Switzerland"),
	de_DE("German", "Germany"),
	en_GB("English", "Great Britain"),
	en_IE("English", "Ireland"),
	es_ES("Spanish", "Spain"),
	el_GR("Greek", "Greece"),
	et_EE("Estonian", "Estonia"),
	fi_FI("Finnish", "Finland"),
	fr_FR("French", "France"),
	fr_BE("French", "Belgium"),
	fr_CA("French", "Canada"),

	fr_CH("French", "Switzerland"),
	hr_HR("Croatian", "Croatia"),	
	hu_HU("Hungarian", "Hungary"),
	it_IT("Italian", "Italy"),
	it_CH("Italian", "Switzerland"),
	lt_LT("Lithuanian", "Lithuania"),
	lv_LV("Latvian", "Latvia"),
	mk_MK("Macedonian", "Macedonia"),
	nb_NO("Norwegian (Bokmål)", "Norway"),
	nl_NL("Dutch", "Netherlands"),
	nl_BE("Dutch", "Belgium"),
	nn_NO("Norwegian (Nynorsk)", "Norway"),
	pl_PL("Polish", "Poland"),
	pt_PT("Portuguese", "Portugal"),
	ro_RO("Romanian", "Romania"),
	ru_BY("Russian", "Belarus"),
	ru_RU("Russian", "Russia"),
	ru_UA("Russian", "Ukraine"),
	ru_KZ("Russian", "Kazakhstan"),
	sk_SK("Slovak", "Slovakia"),
	sl_SI("Slovene", "Slovenia"),
	sq_AL("Albanian", "Albania"),
	sr_SP("Serbian", "Serbia"),
	sv_SE("Swedish", "Sweden"),
	tr_TR("Turkish", "Turkey"),
	uk_UA("Ukrainian", "Ukraine"),
	sr_RS("Serbian", "Republic of Serbia"),
	
	
	//Middle-East
	ar_MA("Arabic", "Morocco"),
	ar_EG("Arabic", "Egypt"),
	ar_AE("Arabic", "U.A.E."),
	ar_SA("Arabic", "Saudi Arabia"),
	fa_IR("Farsi", "Iran"),
	en_SA("English", "Saudi Arabia"),
	he_IL("Hebrew", "Israel"),
	ar_BH("Arabic", "Bahrain"),
	ar_KW("Arabic", "Kuwait"),
	ar_OM("Arabic", "Oman"),
	ar_QA("Arabic", "Qatar"),
	en_AE("English", "UAE"),
	en_LB("English", "Lebanon"),
	en_ME("English", "Middle East"),
	ar_ME("Arabic", "Middle East"),
	en_JO("English", "Jordan"),
	
	
	//Africa
	en_KE("English", "Kenya"),
	en_TG("English", "Togo"),
	en_ZA("English", "South Africa"),
	fr_ZA("French", "South Africa"),
	
	// Asia
	en_HK("English", "Hong-Kong"),
	en_ID("English", "Indonesia"),
	en_IN("English", "India"),
	en_MY("English", "Malaysia"),
	en_PH("English", "Philippines"),
	en_SG("English", "Singapore"),
	en_TH("English", "Thailand"),
	en_CN("English", "China"),
	en_JP("English", "Japan"),
	hi_IN("Hindi", "India"),
	id_ID("Indonesian", "Indonesia"),
	ja_JP("Japanese", "Japan"),
	ko_KR("Korean", "Korea"),
	my_MM("Burmese", "Myanmar"),
	ms_MY("Malay", "Malaysia"),
	vi_VN("Vietnamese", "Vietnam"),
	th_TH("Thai", "Thailand"),
	zh_CN("Chinese", "China"),
	zh_CHS("Chinese, Simplified", "China"),
	zh_CHT("Chinese, Traditional", "China"),
	zh_HK("Chinese, Hong-Kong (Traditional)", "China"),
	zh_TW("Chinese, Taiwan (Traditional)", "China"),
	
	// Oceania
	en_AU("English","Australia"),
	
	// Worldwide
	en_WW("English", "Worldwide");


	
	public String language;
	public String country;
	public String code;
	
	
	Language(String language, String country){
		this.language = language;
		this.country = country;
		this.code = this.name().replace("_", "-");
	}
	
    public static Language forValue(String languageString) {
		if(StringUtils.isBlank(languageString)){
			return null;
		}
		String languageStringCommon = languageString.replace("_","-").toLowerCase();
        Language language = Arrays.asList(Language.values()).stream().filter(x -> x.code.toLowerCase().equals(languageStringCommon)).findFirst().orElse(null);
        return language;
    }

	public static List<Language> forValue(List<String> value) {
		return value.stream().map(x -> forValue(x)).collect(Collectors.toList());
    }

	
}
