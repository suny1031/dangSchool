package com.dang.member.school.model.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.dang.board.model.vo.Board;
import com.dang.common.code.ConfigCode;
import com.dang.common.exception.DataAccessException;
import com.dang.common.exception.ToAlertException;
import com.dang.common.jdbc.JDBCTemplate;
import com.dang.common.mail.MailSender;
import com.dang.common.util.file.FileUtil;
import com.dang.common.util.file.FileVo;
import com.dang.map.model.vo.Service;
import com.dang.member.school.model.dao.SchoolDao;
import com.dang.member.school.model.vo.SchoolMember;
import com.dang.member.user.model.vo.UserMember;

//service 단에서는 비즈니스 로직을 작성하게 된다.
//비즈니스 로직에 필요한 데이터를 Controller에게 전달받고 추가적으로 필요한 데이터를 Dao에게 요청하여
//핵심 로직인 비즈니스 로직을 작성한다.
//비즈니스 로직을 service가 담당하기 때문에 transaction관리도 service가 담당.

public class SchoolService {

	SchoolDao schoolDao = new SchoolDao();
	JDBCTemplate jdt = JDBCTemplate.getInstance();

	// 로그인시 아이디와 비밀번호를 확인하는 비즈니스 로직
	public SchoolMember memberAuthenticate(String kgId, String kgPw) {
		// connection 생성
		Connection conn = jdt.getConnection();
		SchoolMember schoolMember = null;

		try {
			// Dao에게 Connection 주입
			// Dao는 주입받은 Connection객체로 Statement 객체를 만들어 쿼리를 실행하게 되고
			// 따라서 Service에서 해당 쿼리를 commit, rollback처리를 할 수 있게 된다.
			// Dao에서 schoolMembe 객체가 반환된다.
			schoolMember = schoolDao.memberAuthenticate(conn, kgId, kgPw);

		} finally {
			jdt.close(conn);
		}

		return schoolMember;

	}

	public Service schoolProService(String kgName) {
		Connection conn = jdt.getConnection();
		Service schoolProService = null;

		try {
			schoolProService = schoolDao.schoolProService(conn, kgName);
		} finally {
			jdt.close(conn);
		}

		return schoolProService;
	}

	public SchoolMember findSchoolId(String schoolName, String schoolPhone) {

		Connection conn = jdt.getConnection();
		SchoolMember res = null;

		try {
			res = schoolDao.findSchoolId(conn, schoolName, schoolPhone);
		} finally {
			jdt.close(conn);
		}

		return res;

	}

	/*
	 * 
	 * public SchoolMember findSchoolPw(String kgId, String kgEmail) {
	 * 
	 * Connection conn = jdt.getConnection(); SchoolMember schoolMember = null;
	 * 
	 * try{ schoolMember = schoolDao.findSchoolPw(conn, kgId, kgEmail);
	 * 
	 * }finally { jdt.close(conn); } return schoolMember;
	 * 
	 * }
	 */

	// 비밀번호 찾기시 이메일보내기
	public void finSchoolPwEmail(SchoolMember schoolMember) {

		String subject = "임시 비밀번호가 발급되었습니다.";
		String htmlText = "<h1>'${param.userPw}'</h1><h1>입니다." + "</h1><h1>아래의 링크를 클릭하시면 로그인 창으로 이동합니다."
				+ "</h1><h1>로그인 후 임시비밀번호를 바꿔주세요.</h1>";

		htmlText += "<a href='" + ConfigCode.DOMAIN + "/school/login.do\'>홈페이지 이동하기</a>";
		String to = schoolMember.getKgEmail();

		new MailSender().sendEmail(subject, htmlText, to);

	}

	public int modifySchoolInfo(String kgId, String kgName, String kgAddress, String kgTell, String kgOperateTime,
			String kgNotice, String kgEmail) {

		Connection conn = jdt.getConnection();
		int res = 0;

		try {
			res = schoolDao.modifySchoolInfo(conn, kgId, kgName, kgAddress, kgTell, kgOperateTime, kgNotice, kgEmail);
			jdt.commit(conn);

		} catch (DataAccessException e) {
			jdt.rollback(conn);
		} finally {
			jdt.close(conn);
		}

		return res;
	}

	public int modifySchoolService(String kgName, int isKg, int isCafe, int isHotel, int isPickup, int isMedic,
			int isAcademy, int isSpa) {
		Connection conn = jdt.getConnection();
		int res = 0;

		try {
			res = schoolDao.modifySchoolService(conn, kgName, isKg, isCafe, isHotel, isPickup, isMedic, isAcademy,
					isSpa);
			jdt.commit(conn);
		} catch (DataAccessException e) {
			jdt.rollback(conn);
		} finally {
			jdt.close(conn);
		}
		return res;

	}

	public int uploadSchoolPhoto(SchoolMember schoolMember, HttpServletRequest request) {
		Connection conn = jdt.getConnection();
		int res = 0;
		String kgIdx = schoolMember.getKgIdx();
		// 게시글 저장
		Map<String, List> PhotoData = new FileUtil().fileUpload(request);

		try {

			for (FileVo schoolPhotoData : (List<FileVo>) PhotoData.get("fileData")) {
				schoolDao.uploadSchoolPhoto(conn, schoolPhotoData, kgIdx);
			}
			jdt.commit(conn);
		} catch (DataAccessException e) {
			jdt.rollback(conn);
			throw new ToAlertException(e.error, e);
		} finally {
			jdt.close(conn);
		}
		System.out.println(res);
		return res;

	}

	public ArrayList<FileVo> selectSchoolPhoto(String kgIdx) {
		Connection conn = jdt.getConnection();
		ArrayList<FileVo> photoList;

		try {
			photoList = schoolDao.selectSchoolPhoto(conn, kgIdx);
		} finally {
			jdt.close(conn);
		}
		return photoList;
	}

	public ArrayList<Board> selectNoticePreview(String kgName) {
		Connection conn = jdt.getConnection();
		ArrayList<Board> noticeList;

		try {
			noticeList = schoolDao.selectNoticePreview(conn, kgName);
		} finally {
			jdt.close(conn);
		}
		return noticeList;
	}

	public ArrayList<UserMember> selectClassMember(String kgName) {
		Connection conn = jdt.getConnection();
		ArrayList<UserMember> classMemberList;

		try {
			classMemberList = schoolDao.selectClassMember(conn, kgName);

		} finally {
			jdt.close(conn);
		}
		return classMemberList;
	}

	public UserMember findClassMemberById(String userId) {
		Connection conn = jdt.getConnection();
		// 반환할 유저 객체 생성
		UserMember userMember = null;

		try {

			userMember = schoolDao.findClassMemberById(conn, userId);

		} finally {
			jdt.close(conn);
		}

		return userMember;
	}

	public int regUserMember(String kgName, String userId) {

		Connection conn = jdt.getConnection();
		int res = 0;

		try {

			res = schoolDao.regUserMember(conn, kgName, userId);
			jdt.commit(conn);

		} catch (DataAccessException e) {
			jdt.rollback(conn);
			throw new ToAlertException(e.error, e);
		} finally {
			jdt.close(conn);
		}

		System.out.println(res);
		return res;

	}

	public int deleteClassMember(String userId) {
		Connection conn = jdt.getConnection();
		int res = 0;

		try {
			res = schoolDao.deleteClassMember(conn, userId);
			jdt.commit(conn);
		} catch (DataAccessException e) {
			jdt.rollback(conn);
		} finally {
			jdt.close(conn);
		}
		return res;
	}

	// 유저 - 유치원 페이지 페이징
	public List<UserMember> selectBoardPage(int startRow, int endRow, String kgName) {
		Connection conn = jdt.getConnection();
		List<UserMember> list;
		try {
			list = schoolDao.selectUserMemberPage(conn, startRow, endRow, kgName);

		} finally {
			jdt.close(conn);
		}
		return list;

	}

	// 유저 - 유치원 총 개수 메서드
	public int selectCountPage(String kgName) {
		Connection conn = jdt.getConnection();

		int count;
		try {
			count = schoolDao.selectCountPage(conn, kgName);
		} finally {
			jdt.close(conn);
		}
		return count;

	}

}
