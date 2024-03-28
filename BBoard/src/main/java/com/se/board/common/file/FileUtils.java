package com.se.board.common.file;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.se.board.domain.file.FileRequest;
import com.se.board.domain.file.FileResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileUtils {

	private final String uploadPath = Paths.get("C:","develop", "upload-files").toString();

	/**
	 * 다중 파일 업로드
	 * @param multipartFiles - 파일 객체 List
	 * @return DB에 저장할 파일 정보 List
	 */
	public List<FileRequest> uploadFiles(final List<MultipartFile> multipartFiles) {
		List<FileRequest> files = new ArrayList<>();

		for(MultipartFile multipartFile : multipartFiles) {
			if(multipartFile.isEmpty()) {
				continue;
			}
			files.add(uploadFile(multipartFile));
		}

		return files;
	}

	/**
	 * 단일 파일 업로드
	 * @param multipartFile - 파일 객체
	 * @return DB에 저장할 파일 정보
	 */
	public FileRequest uploadFile(MultipartFile multipartFile) {

		if(multipartFile.isEmpty()) {
			return null;
		}

		String saveName = generateSaveFileName(multipartFile.getOriginalFilename());
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
		String uploadPath = getUploadPath(today) + File.separator + saveName;
		File uploadFile = new File(uploadPath);

		try {
			multipartFile.transferTo(uploadFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return FileRequest.builder()
				.originalName(multipartFile.getOriginalFilename())
				.saveName(saveName)
				.size(multipartFile.getSize())
				.addPath(today)
				.build();
	}

	/**
	 * 저장 파일명 생성
	 * @param filename 원본 파일명
	 * @return 디스크에 저장할 파일명
	 */
	public String generateSaveFileName(final String filename) {
		String uuid = UUID.randomUUID().toString().replace("-", ""); // 32자리 랜덤 문자
		String extension = StringUtils.getFilenameExtension(filename);

		return uuid + "." + extension;
	}

	/**
	 * 업로드 경로 반환
	 * @return 업로드 경로
	 */
	public String getUploadPath() {
		return makeDirectories(uploadPath);
	}

	/**
	 * 업로드 경로 반환
	 * @param addPath - 추가 경로
	 * @return 업로드 경로
	 */
	public String getUploadPath(final String addPath) {
		return makeDirectories(uploadPath + File.separator + addPath);
	}

	/**
	 * 업로드 폴더(디렉토리) 생성
	 * @param path - 업로드 경로
	 * @return 업로드 경로
	 */
	public String makeDirectories (final String path) {
		File dir = new File(path);
		if(dir.exists() == false) {
			dir.mkdirs();
		}
		return dir.getPath();
	}



	/**
	 * 파일 삭제 (from Disk)
	 * @param files - 삭제할 파일 정보 List
	 */
	public void deleteFiles(final List<FileResponse> files) {
		if(CollectionUtils.isEmpty(files)) {
			return;
		}

		for (FileResponse file : files) {
			//String uploadedDate = file.getCreatedDate().toLocalDate().format(DateTimeFormatter.ofPattern("yyMMdd")); // toLocalDate() 오류나는데.. 이미 타입이 LocalDate 타입인데....
			String uploadedDate = file.getCreatedDate().format(DateTimeFormatter.ofPattern("yyMMdd"));
			deleteFile(uploadedDate, file.getSaveName());
		}
	}

	/**
	 * 파일 삭제 (from Disk)
	 * @param addPath - 추가 경로
	 * @param filename - 파일명
	 */
	public void deleteFile(final String addPath, final String filename) {
		String filePath = Paths.get(uploadPath, addPath, filename).toString();
		deleteFile(filePath);
	}

	/**
	 * 파일 삭제 (from Disk)
	 * @param filePath - 파일 경로
	 */
	public void deleteFile(String filePath) {
		File file = new File(filePath);
		if(file.exists()) {
			file.delete();
		}
	}

	public Resource readFileAsResource(final FileResponse file) {
		String uploadedDate = file.getCreatedDate().format(DateTimeFormatter.ofPattern("yyMMdd"));
		String filename = file.getSaveName();
		Path filePath = Paths.get(uploadPath, uploadedDate, filename);

		try {
			Resource resource = new UrlResource(filePath.toUri());
			if(resource.exists() == false || resource.isFile() == false) {
				throw new RuntimeException("file not found : " + filePath.toString());
			}
			return resource;
		} catch (MalformedURLException e) {
			throw new RuntimeException("file not found : " + filePath.toString());
		}
	}

	// add
	public void encodeFiles(List<FileRequest> files) {
		if(CollectionUtils.isEmpty(files)) {
			return;
		}

		for(FileRequest file : files) {
			if(file == null) {
				continue;
			}
			log.debug("file id:" + file.getId() + ", addPath: " + file.getAddPath() + ", saveName: " + file.getSaveName()+ ", originalName: " + file.getOriginalName() );
			String filePath = Paths.get(uploadPath, file.getAddPath(), file.getSaveName()).toString();
			File a = new File (filePath);
			if ( !a.exists()) {
				continue;
			}

			file.setB64Str(FileToBase64Encoder.encodeFileToBase64(a));
			log.debug("file.getB64Str() : " + file.getB64Str());

		}

	}

}
