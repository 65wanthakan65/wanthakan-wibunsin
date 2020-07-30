package com.challenge.healthcheck.rest.service;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class HealthcheckService {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	public List<String> getDataFromCSV(MultipartFile file) throws Exception {
		List<String> list = new ArrayList<>();

		CSVReader reader = null;
		try {
			reader = new CSVReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
			String[] line;
			while ((line = reader.readNext()) != null) {
				for(int i=0 ; i<line.length ; i++) {
					if(StringUtils.isNoneBlank(line[i])) list.add(line[i]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		return list;
	}
	
	
	 public boolean isServerReachable(String url) {
        try {
        	if(url.indexOf("http") < 0) {
        		url = "http://"+url;
        	}
        		
			URL urlServer = new URL(url);
			HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
			urlConn.setConnectTimeout(3000); // <- 3Seconds Timeout
			urlConn.connect();
			if (urlConn.getResponseCode() == 200) {
				return true;
			} else {
				return false;
			}
		} catch (MalformedURLException e1) {

			return false;
		} catch (IOException e2) {
//			e2.printStackTrace();
			return false;
		}catch (Exception e3) {
//			e3.printStackTrace();
			return false;
		}
	}
		
}
