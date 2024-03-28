package com.se.board.domain.file;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileService {

	private final FileMapper fileMapper;

	/**
	 * 파일 정보 저장
	 * @param postId - 게시글 번호 (FK)
	 * @param files 파일 정보 리스트
	 */
	@Transactional
	public void saveFiles(final Long postId, final List<FileRequest> files) {
		if(CollectionUtils.isEmpty(files)) {
			return;
		}

		for(FileRequest file : files) {
			file.setPostId(postId);
		}

		fileMapper.saveAll(files);
	}

	@Transactional
	public void saveFilesEach(final Long postId, String writer, final List<FileRequest> files) {
		if(CollectionUtils.isEmpty(files)) {
			return;
		}

		for(FileRequest file : files) {
			file.setPostId(postId);
			file.setWriter(writer);
			saveFile(file);
		}

	}

	public void saveFile(final FileRequest params) {
		fileMapper.save(params);
	}

	/**
	 * 파일 리스트 조회
	 * @param postId - 게시글 번호 (FK)
	 * @return 파일 리스트
	 */
	public List<FileResponse> findAllFileByPostId(final Long postId) {
		return fileMapper.findAllByPostId(postId);
	}

	/**
	 * 파일 리스트 조회
	 * @param ids - PK 리스트
	 * @return 파일 리스트
	 */
	public List<FileResponse> findAllFileByIds(final List<Long> ids) {
		if(CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>(); // return Collections.emptyList(); emptyList() 메소드가 없다고 나와서...
		}
		return fileMapper.findAllByIds(ids);
	}

	/**
	 * 파일 삭제
	 * @param ids - PK 리스트
	 */
	@Transactional
	public void deleteAllFileByIds(final List<Long> ids) {
		if(CollectionUtils.isEmpty(ids)) {
			return;
		}
		fileMapper.deleteAllByIds(ids);
	}

	/**
	 * 파일 상세정보 조회
	 * @param id - PK
	 * @return 파일 상세정보
	 */
	public FileResponse findFileById(final Long id) {
		return fileMapper.findById(id);
	}

	/**
	 * 파일 삭제 (게시글 삭제 시)
	 * @param postId - FK
	 */
	@Transactional
	public void deleteAllFileByPostId(final Long postId) {
		fileMapper.deleteAllByPostId(postId);
	}

}
