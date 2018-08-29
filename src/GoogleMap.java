import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
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
		File rootDir = new File("testdata//raw");
		for (File file : rootDir.listFiles()) {
			System.out.println(file.getAbsolutePath());
			// text 파일에서 지역을 불러옴
			ArrayList<String> places = getPlaceList(file);

			// 구글map api를 활용하여, 각 관광지의 placeId를 얻어옴
			ArrayList<String> placeIds = getPlaceIdList(context, places);

			// 구글map api를 활용하여, 각 관광지의 정보를 얻어옴
			String content = getReviewInfo(context, placeIds);
			System.out.println(content);
			// 가져온 정보를 파일로 씀
			File resultFile = new File("testdata//result//" + file.getName().replace(".txt", ".csv"));
			writeTextFile(content, resultFile);
		}
	}

	static void writeTextFile(String content, File resultFile) throws IOException {
		FileWriter fw = new FileWriter(resultFile);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		if (bw != null) {
			bw.close();
		}
		if (fw != null) {
			fw.close();
		}
	}

	static ArrayList<String> getPlaceIdList(GeoApiContext context, ArrayList<String> places)
			throws ApiException, InterruptedException, IOException {
		ArrayList<String> placeIds = new ArrayList<>();
		for (String placeName : places) {
			GeocodingResult[] results = GeocodingApi.geocode(context, placeName).awaitIgnoreError();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			if (results.length == 0) {
				continue;
			}
			String placeId = gson.toJson(results[0].placeId).replace("\"", "");
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

	static String getReviewInfo(GeoApiContext context, ArrayList<String> placeIds)
			throws ApiException, InterruptedException, IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String content = "";
		for (String placeId : placeIds) {
			PlaceDetailsRequest pdr = new PlaceDetailsRequest(context);
			pdr.placeId(placeId);
			PlaceDetails pd = pdr.awaitIgnoreError();
			if (pd != null) {
				System.out.println("place name : " + pd.name);
				System.out.println("place id : " + pd.placeId);
				System.out.println("average rating : " + pd.rating);
				if (pd.reviews != null) {
					for (int i = 0; i < pd.reviews.length; i++) {
						if (pd.reviews[i].authorUrl == null) {
							continue;
						}
						if (pd.reviews[i].authorUrl.toString().split("/")[5] == null) {
							continue;
						}
						System.out.println("reviews " + i + " authorName : " + pd.reviews[i].authorName);
						System.out.println(
								"reviews " + i + " authorId : " + pd.reviews[i].authorUrl.toString().split("/")[5]);
						System.out.println("reviews " + i + " rating : " + pd.reviews[i].rating);
						System.out.println("reviews " + i + " text : " + pd.reviews[i].text);
						org.joda.time.Instant it = pd.reviews[i].time;
						System.out.println("reviews " + i + " time : " + it.toString());
						String lineContent = pd.placeId + "\t" + pd.name + "\t" + pd.rating + "\t"
								+ pd.reviews[i].authorUrl.toString().split("/")[5] + "\t" + pd.reviews[i].authorName
								+ "\t" + pd.reviews[i].rating + "\t" + pd.reviews[i].text + "\t" + pd.reviews[i].time
								+ "\n";
						content += lineContent;
					}
				}
			}
		}
		return content;
	}

	static String getReviewerId(String url) {
		String[] urlFactors = url.split("/");
		return urlFactors[5];
	}

}
