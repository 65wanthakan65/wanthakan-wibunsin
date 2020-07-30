package com.challenge.healthcheck.rest.api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.challenge.healthcheck.linecorp.login.infra.line.api.v2.LineAPIService;
import com.challenge.healthcheck.linecorp.login.infra.line.api.v2.response.AccessToken;
import com.challenge.healthcheck.rest.bean.ResponseApi;
import com.challenge.healthcheck.rest.bean.ResponseHealthcheck;
import com.challenge.healthcheck.rest.service.HealthcheckService;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;

@RestController
@RequestMapping("/healthcheck")
@Tag(name = "healthcheck")
public class HealthcheckResources {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private HealthcheckService healthcheckService;

	@Autowired
	private LineAPIService lineAPIService;

	
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = {
			"multipart/form-data" }, produces = "application/json")
	@Operation(summary = "upload CSV File")
	public ResponseApi upload(@RequestParam("file") MultipartFile file) {
		
		// Get current time
		long start = System.currentTimeMillis();

		LOG.info("Uploaded File: ");
		List<String> urlList = new ArrayList<String>();
		ResponseHealthcheck res = new ResponseHealthcheck();
		try {
			if (file == null) {
				return new ResponseApi<>(Response.Status.INTERNAL_SERVER_ERROR, "File is null!! ");
			}
			LOG.info("Name : " + file.getOriginalFilename());
			LOG.info("Size : " + file.getSize());

			final String fileType = FilenameUtils.getExtension(file.getOriginalFilename());
			LOG.info("fileType : " + fileType);
			if (!fileType.equalsIgnoreCase("csv")) {
				return new ResponseApi<>(Response.Status.INTERNAL_SERVER_ERROR, "FileType is not CSV!! ");
			}

			urlList = healthcheckService.getDataFromCSV(file);
//            LOG.info(ArrayUtils.toString(urlList));
			if (CollectionUtils.isEmpty(urlList)) {
				return new ResponseApi<>(Response.Status.INTERNAL_SERVER_ERROR, "CSV File is Empty!! ");
			}
			
			int size = urlList.size();
			int success = 0;
			int failure = 0;
			for(String url: urlList) {
				if(healthcheckService.isServerReachable(url)) success++;
				else failure++;
//				LOG.info(url);
			}
			
			// Get elapsed time in milliseconds
			long elapsedTimeMillis = System.currentTimeMillis()-start;
			float elapsedTimeSec = elapsedTimeMillis/1000F;
			float elapsedTimeMin = elapsedTimeMillis/(60*1000F);
			
			LOG.info("size : {},success : {},failure : {}",size,success,failure);
			LOG.info("time : {}, {}, {}",elapsedTimeMillis,elapsedTimeSec,elapsedTimeMin);
			
			res.setFailure(failure);
			res.setSuccess(success);
			res.setTotal_time(elapsedTimeMillis);
			res.setTotal_websits(size);
			
			lineAPIService.sendMsg("FileName: "+ file.getOriginalFilename() +"\n"
					+ "Checked websites: "+ size +"\n"
					+ "Successful websites: "+ success +"\n"
					+ "Failure websites: "+ failure +"\n"
					+ "Totaltime to finished checking websites: "+ elapsedTimeMillis+" , "
					+elapsedTimeSec+" , " +elapsedTimeMin +" (ms, sec, minutes)");
			
		} catch (WebApplicationException ex) {
			throw ex;
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
		return new ResponseApi<>(Response.Status.OK, res);
	}
}
