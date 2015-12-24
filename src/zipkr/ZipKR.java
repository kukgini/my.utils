package zipkr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import org.apache.tools.zip.ZipEntry; //ant.jar
import org.apache.tools.zip.ZipOutputStream; //ant.jar

public class ZipKR {

	private static final String ENCODING = "euc-kr";
	private static final String SEPERATOR = "/";
	
	private boolean keepDirStructure = true;

	public static void main(String args[]) throws Exception {
		ZipKR compres = new ZipKR();

		String[] test = { "d:/test/c" };// 압축할 파일과 디렉토리....
		compres.compressZIP("d:/test/c.zip", test, true); // compresora.comprimirZIP("출력되는이름",
														// "압축할파일", true);
	}

	public boolean compressZIP(String target, String[] source,
			boolean keepDirStructure) throws Exception {
		this.keepDirStructure = keepDirStructure;

		File[] files = new File[source.length];

		// 파일이나 디렉터리가 아니라면 이상한 유형의 파일이니 압축대상은 아니다.
		for (int i = 0; i < source.length; i++) {
			files[i] = new File(source[i]);
			if (!files[i].isFile() && !files[i].isDirectory()) {
				return false;
			}
		}

		// 타겟파일을 생성할 장소에 디렉터리 생성이 안되어 있으면 만든다.
		String rutaDestino = "";
		if (target.indexOf(SEPERATOR) != -1) {
			rutaDestino = target.substring(0, target.lastIndexOf(SEPERATOR) + 1);
			mkdirs(rutaDestino);
		}

		FileOutputStream fos = new FileOutputStream(target);
		ZipOutputStream zos = new ZipOutputStream(fos);
		zos.setEncoding(ENCODING);
		zos.setLevel(3);
		for (int i = 0; i < files.length; i++) {
			zipEntries(zos, target, files[i]);
		}
		// zos.finish(); // 요 녀석을 주석처리 하지 않으면 일반적인 압축툴로 풀어지지가 않는다.
		                 // 왜 그런지 모르겠으나 우선 문제를 해결하기 위해 주석으로  막아둔다.
		zos.close();
		return true;
	}

	private void zipEntries(ZipOutputStream zos, String target, File source) throws Exception {
		int byteCount;
		final int DATA_BLOCK_SIZE = 2048;
		if (source.isDirectory()) {
			File[] files = source.listFiles();
			for (int i = 0; i < files.length; i++) {
				zipEntries(zos, target, files[i]);
			}
		} 
		else 
		{
			// 원본과 대상이 같은 파일이라면 압축이 안되쟎았 !!! 무시하고 지나가자.
			if (source.getAbsolutePath().equalsIgnoreCase(target)) {
				return;
			}

			source.length();
			String strAbsPath = source.getPath();

			// 구조를 존중한다는 것은 라이브러리 사용자가  source 를 절대경로로
			// 던졌을 경우 이 절대경로를 가능한 그대로 유지한다는 의미가 있다.
			// 예를 들어  source 가 C:\\somedir\\subdir\\subsubdir 인 경우
			// keepDirStructure 가 true 라면 zip 파일에 들어갈 entry 의 이름은
			// somedir/subdir/subsubdir 이 된다.
			// 반면, keepDirStructure 가 false 라면 subsubdir 이 된다.
			String strZipEntryName = "";
			if (keepDirStructure) {
				if (strAbsPath.indexOf(":") != -1) {
					strZipEntryName = strAbsPath.substring(strAbsPath
							.indexOf(":") + 1);
				} else {
					strZipEntryName = strAbsPath;
				}
			} else {
				strZipEntryName = strAbsPath.substring(strAbsPath
						.lastIndexOf(SEPERATOR) + 1);
			}
			
			// Unix 스타일의 파일 경로 표기법으로 바꾼다. 이래야 특정 프로그램에서
			// 제대로 안풀리는 문제를 피해갈 수 있다.
			strZipEntryName = strZipEntryName.replaceAll("\\\\", "\\/");
			
			// 파일명의 경로가 절대경로 (맨 앞에 / 가 붙은것) 으로 되어있으면
			// 특정 프로그램에서 제대로 안풀리는 문제를 피해갈 수 있다.
			// 내가 알기로 우선  7-Zip 의 윈도우 버전이 제대로 안풀리더라는...
			if (strZipEntryName.charAt(0) == '/') {
			    strZipEntryName = strZipEntryName.substring(1);
			}

			FileInputStream fis = new FileInputStream(source);
			ZipEntry cpZipEntry = new ZipEntry(strZipEntryName);
			zos.putNextEntry(cpZipEntry);
			byte[] b = new byte[DATA_BLOCK_SIZE];
			while ((byteCount = fis.read(b, 0, DATA_BLOCK_SIZE)) != -1) {
				zos.write(b, 0, byteCount);
			}
			zos.closeEntry();
			fis.close();
		}
	}

	private void mkdirs(String ruta) {
		File dirs = new File(ruta);
		if (!dirs.exists()) {
			dirs.mkdirs();
		}
	}
}
