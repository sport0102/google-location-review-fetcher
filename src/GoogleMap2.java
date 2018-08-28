import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlaceDetails;

public class GoogleMap2 {
	public static void main(String[] args) throws IOException, ApiException, InterruptedException {
		File rootDir = new File("C:\\korTourResult");
		// 구글 api 사용 전 세팅
		String googleMapApiKey = "AIzaSyCtH23Lzoq_-6L1lU0DRNrO66rYBy10oGM";
		GeoApiContext context = new GeoApiContext.Builder().apiKey(googleMapApiKey).build();
		// 파일 찾아서 시작
		for (File file : rootDir.listFiles()) {
			ArrayList<String> placeIds = getIdList(file);

			// 각 관광지의 placeId를 얻어옴
			getReviewInfo(context, placeIds);
		}
	}

	static void getReviewInfo(GeoApiContext context, ArrayList<String> placeIds)
			throws ApiException, InterruptedException, IOException {
		for (String placeId : placeIds) {
			PlaceDetailsRequest pdr = new PlaceDetailsRequest(context);
			pdr.placeId(placeId);
			PlaceDetails pd = pdr.awaitIgnoreError();
			if (pdr != null) {
				System.out.println("place name : " + pd.name);
				System.out.println("reviews : " + pd.reviews);
			}
		}
	}

	static ArrayList<String> getIdList(File file) throws IOException {
		FileReader fr;
		BufferedReader br;
		ArrayList<String> places = new ArrayList();
		fr = new FileReader(file);
		br = new BufferedReader(fr);
		String line = "";
		places.clear();
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			places.add(line);
		}
		System.out.println("장소 ids : " + places.toString());
		System.out.println("장소 id 개수 : " + places.size());
		if (br != null) {
			br.close();
		}
		if (fr != null) {
			fr.close();
		}
		return places;
	}
}
