package kr.co.todaydaeng.member.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import kr.co.todaydaeng.member.model.vo.EmailAuthHist;
import kr.co.todaydaeng.member.model.vo.Member;

@Repository
public class MemberDAO {
	
	@Autowired
	@Qualifier(value="sqlSessionTemplate")
	private SqlSessionTemplate sqlSession;
	
	//회원가입
	public int joinMember(Member member) {
		return sqlSession.insert("member.insertMember",member);
	}
	
	//회원가입 id 중복 확인
	public int memberIdCheck(String memberId) {
		return sqlSession.selectOne("member.selectId",memberId)!=null?1:0;
	}
	
	//로그인
	public Member loginMember(Member member) {
		return sqlSession.selectOne("member.loginMember",member);
	}
	
	//id찾기 전 회원 여부 확인
	public Member memberEmailCheck(String email) {
		return sqlSession.selectOne("member.selectEmail",email);
	}
	
	//id찾기 시 이메일 인증 정보 저장
	public int insertAuthNo(EmailAuthHist emailAuthHist) {
		return sqlSession.insert("member.insertAuthNo",emailAuthHist);
	}
	
	//기존 소셜 회원인지 id값 확인
	public Member selectSocialId(String socialId) {
		return sqlSession.selectOne("member.selectId",socialId);
	}
}
