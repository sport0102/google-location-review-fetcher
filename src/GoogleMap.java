import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;

public class GoogleMap {
	public static void main(String[] args) throws ApiException, InterruptedException, IOException {
		// 구글 api 사용 전 세팅
		String googleMapApiKey = "AIzaSyCtH23Lzoq_-6L1lU0DRNrO66rYBy10oGM";
		GeoApiContext context = new GeoApiContext.Builder().apiKey(googleMapApiKey).build();
		// txt 파일에서 관광지 추출
		File rootDir = new File("C:\\korTour");
		for (File file : rootDir.listFiles()) {
			ArrayList<String> places = getPlaceList(file);

			// 구글map api를 활용하여, 각 관광지의 placeId를 얻어옴
			ArrayList<String> placeIds = getPlaceIdList(context, places);
			writeFile(file.getName(), placeIds);
		}
	}

	static void writeFile(String fileName, ArrayList<String> placeIds) throws IOException {
		String content = "";
		String placeFilePath = "C:\\korTourResult\\" + fileName;
		for (String placeId : placeIds) {
			content += placeId + "\n";
		}
		System.out.println(content);
		File writeFile = new File(placeFilePath);
		BufferedWriter bw = new BufferedWriter(new FileWriter(writeFile));
		if (writeFile.isFile() && writeFile.canWrite()) {
			bw.write(content);
		}
		bw.close();
	}

	static ArrayList<String> getPlaceIdList(GeoApiContext context, ArrayList<String> places)
			throws ApiException, InterruptedException, IOException {
		ArrayList<String> placeIds = new ArrayList<>();
		for (String placeName : places) {
			GeocodingResult[] results = GeocodingApi.geocode(context, placeName).awaitIgnoreError();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String placeId = gson.toJson(results[0].placeId);
			placeIds.add(placeId);
			System.out.println("place name : " + placeName);
			System.out.println("place id : " + placeId);
		}
		return placeIds;
	}

	static ArrayList<String> getPlaceList(File file) throws IOException {
		FileReader fr;
		BufferedReader br;
		ArrayList<String> places = new ArrayList();
		fr = new FileReader(file);
		br = new BufferedReader(fr);
		String line = "";
		places.clear();
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			String[] words = line.split("\t");
			System.out.println("장소 : " + words[2]);
			places.add(words[2]);
			System.out.println();
		}
		System.out.println("장소들 : " + places.toString());
		System.out.println("장소 개수 : " + places.size());
		if (br != null) {
			br.close();
		}
		if (fr != null) {
			fr.close();
		}
		return places;
	}
}
