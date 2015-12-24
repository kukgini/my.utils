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

		String[] test = { "d:/test/c" };// ������ ���ϰ� ���丮....
		compres.compressZIP("d:/test/c.zip", test, true); // compresora.comprimirZIP("��µǴ��̸�",
														// "����������", true);
	}

	public boolean compressZIP(String target, String[] source,
			boolean keepDirStructure) throws Exception {
		this.keepDirStructure = keepDirStructure;

		File[] files = new File[source.length];

		// �����̳� ���͸��� �ƴ϶�� �̻��� ������ �����̴� �������� �ƴϴ�.
		for (int i = 0; i < source.length; i++) {
			files[i] = new File(source[i]);
			if (!files[i].isFile() && !files[i].isDirectory()) {
				return false;
			}
		}

		// Ÿ�������� ������ ��ҿ� ���͸� ������ �ȵǾ� ������ �����.
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
		// zos.finish(); // �� �༮�� �ּ�ó�� ���� ������ �Ϲ����� �������� Ǯ�������� �ʴ´�.
		                 // �� �׷��� �𸣰����� �켱 ������ �ذ��ϱ� ���� �ּ�����  ���Ƶд�.
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
			// ������ ����� ���� �����̶�� ������ �ȵ���� !!! �����ϰ� ��������.
			if (source.getAbsolutePath().equalsIgnoreCase(target)) {
				return;
			}

			source.length();
			String strAbsPath = source.getPath();

			// ������ �����Ѵٴ� ���� ���̺귯�� ����ڰ�  source �� �����η�
			// ������ ��� �� �����θ� ������ �״�� �����Ѵٴ� �ǹ̰� �ִ�.
			// ���� ���  source �� C:\\somedir\\subdir\\subsubdir �� ���
			// keepDirStructure �� true ��� zip ���Ͽ� �� entry �� �̸���
			// somedir/subdir/subsubdir �� �ȴ�.
			// �ݸ�, keepDirStructure �� false ��� subsubdir �� �ȴ�.
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
			
			// Unix ��Ÿ���� ���� ��� ǥ������� �ٲ۴�. �̷��� Ư�� ���α׷�����
			// ����� ��Ǯ���� ������ ���ذ� �� �ִ�.
			strZipEntryName = strZipEntryName.replaceAll("\\\\", "\\/");
			
			// ���ϸ��� ��ΰ� ������ (�� �տ� / �� ������) ���� �Ǿ�������
			// Ư�� ���α׷����� ����� ��Ǯ���� ������ ���ذ� �� �ִ�.
			// ���� �˱�� �켱  7-Zip �� ������ ������ ����� ��Ǯ�������...
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
