package com.lionmgtllcagencyp.chatapp.modelClasses

class Users {
    private var uid:String = ""
    private var cover:String = ""
    private var profile:String=""
    private var status:String = ""
    private var username:String=""
    private var website:String = ""
    private var instagram:String = ""
    private var facebook:String = ""
    private var search:String = ""

    constructor()

    constructor(
        uid: String,
        cover: String,
        profile: String,
        status: String,
        username: String,
        website: String,
        instagram: String,
        facebook: String,
        search: String
    ) {
        this.uid = uid
        this.cover = cover
        this.profile = profile
        this.status = status
        this.username = username
        this.website = website
        this.instagram = instagram
        this.facebook = facebook
        this.search = search
    }


    fun getUserName():String?{
        return username
    }
    fun setUserName(username: String){
        this.username = username
    }

    fun getCover():String?{
        return cover
    }
    fun setCover(cover: String){
        this.cover = cover
    }

    fun getStatus():String?{
        return status
    }
    fun setStatus(status: String){
        this.status = status
    }

    fun getWebsite():String?{
        return website
    }
    fun setWebsite(website: String){
        this.website = website
    }

    fun getFacebook():String?{
        return facebook
    }
    fun setFacebook(facebook: String){
        this.facebook = facebook
    }

    fun getUID():String?{
        return uid
    }
    fun setUID(uid: String){
        this.uid = uid
    }

    fun getProfile():String?{
        return profile
    }
    fun setProfile(profile: String){
        this.profile = profile
    }

    fun getSearch():String?{
        return search
    }
    fun setSearch(search: String){
        this.search = search
    }
}