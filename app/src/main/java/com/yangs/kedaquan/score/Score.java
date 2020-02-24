package com.yangs.kedaquan.score;

/**
 * Created by yangs on 2017/8/1.
 */

public class Score {
    private String cno;     //课程号
    private String name;    //课程名
    private String score;   //成绩
    private String xf;      //学分
    private String ks;      //课时
    private String khfx;      //考核方式
    private String kcsx;    //课程属性
    private String kcxz;    //课程性质
    private String jd;
    private String jd_term;
    private String term;
    private Boolean isClick = false;
    private Boolean isCheck;
    private Boolean isCBVisil = false;

    String getCno() {
        return cno;
    }

    void setCno(String cno) {
        this.cno = cno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getScore() {
        return score;
    }

    void setScore(String score) {
        this.score = score;
    }

    String getXf() {
        return xf;
    }

    void setXf(String xf) {
        this.xf = xf;
    }

    String getKs() {
        return ks;
    }

    void setKs(String ks) {
        this.ks = ks;
    }

    String getKhfx() {
        return khfx;
    }

    void setKhfx(String khfx) {
        this.khfx = khfx;
    }

    String getKcsx() {
        return kcsx;
    }

    void setKcsx(String kcsx) {
        this.kcsx = kcsx;
    }

    public String getKcxz() {
        return kcxz;
    }

    void setKcxz(String kcxz) {
        this.kcxz = kcxz;
    }

    Boolean getCheck() {
        return isCheck;
    }

    void setCheck(Boolean check) {
        isCheck = check;
    }

    Boolean getCBVisil() {
        return isCBVisil;
    }

    void setCBVisil() {
        isCBVisil = true;
    }

    Boolean getClick() {
        return isClick;
    }

    void setClick(Boolean click) {
        isClick = click;
    }

    String getJd() {
        return jd;
    }

    void setJd(String jd) {
        this.jd = jd;
    }

    String getTerm() {
        return term;
    }

    void setTerm(String term) {
        this.term = term;
    }

    String getJD_Term() {
        return jd_term;
    }

    void setJD_Term(String jd_term) {
        this.jd_term = jd_term;
    }
}
