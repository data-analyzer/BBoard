package com.se.board.domain.file;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {

	/**
	 * 파일 정보 저장
	 * @param files - 파일 정보 리스트
	 */
	void saveAll(List<FileRequest> files);

	void save(FileRequest params);

	/**
	 * 파일 리스트 조회
	 * @param postId - 게시글 번호 (FK)
	 * @return 파일 리스트
	 */
	List<FileResponse> findAllByPostId(Long postId);

	/**
	 * 파일 리스트 조회
	 * @param ids - PK 리스트
	 * @return
	 */
	List<FileResponse> findAllByIds(List<Long> ids);


	/**
	 * 파일 삭제
	 * @param ids - PK 리스트
	 */
	void deleteAllByIds(List<Long> ids);

	/**
	 * 파일 상세정보 조회
	 * @param id - PK
	 * @return 파일 상세정보
	 */
	FileResponse findById(Long id);


	void deleteAllByPostId(Long postId);
}
