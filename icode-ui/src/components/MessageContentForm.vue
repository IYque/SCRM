<template>
  <el-form ref="form" :model="form" :rules="rules" label-width="100px">
    <!-- 图片 -->
    <template v-if="type === dictMsgType.image.type">
      <el-form-item label="" prop="picUrl">
        <Upload v-model:fileUrl="form.picUrl">
          <template #tip><div>图片大小不超过2M</div></template>
        </Upload>
      </el-form-item>
    </template>
    <!-- 链接 -->
    <template v-else-if="type === dictMsgType.link.type">
      <el-form-item label="链接标题" prop="title">
        <el-input
          v-model="form.title"
          type="text"
          :maxlength="32"
          show-word-limit
          placeholder="请输入链接标题"></el-input>
      </el-form-item>
      <el-form-item label="链接描述">
        <el-input
          v-model="form.desc"
          type="textarea"
          :maxlength="120"
          show-word-limit
          :autosize="{ minRows: 3, maxRows: 50 }"
          placeholder="请输入链接描述"></el-input>
      </el-form-item>
      <el-form-item label="链接封面">
        <Upload v-model:fileUrl="form.picUrl"></Upload>
      </el-form-item>
      <el-form-item label="链接地址" prop="url">
        <el-input v-model="form.url" type="text" placeholder="请输入链接地址，以http://或https://开头"></el-input>
      </el-form-item>
    </template>

    <!-- 小程序-->
    <template v-else-if="type === dictMsgType.miniprogram.type">
      <el-form-item label="小程序标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入小程序标题" :maxlength="16" show-word-limit></el-input>
      </el-form-item>
      <el-form-item label="小程序ID" prop="appid">
        <el-input v-model="form.appid" placeholder="小程序AppID"></el-input>
      </el-form-item>
      <el-form-item label="页面路径" prop="page">
        <el-input v-model="form.page" placeholder="请输入小程序路径，必须以 .html 作为后缀"></el-input>
      </el-form-item>
      <el-form-item label="封面" prop="picUrl">
        <Upload v-model:fileUrl="form.picUrl"></Upload>
      </el-form-item>
    </template>

    <!-- 视频 -->
    <template v-else-if="type === dictMsgType.video.type">
      <el-form-item label="上传视频" prop="videoUrl">
        <Upload v-model:fileUrl="form.videoUrl" type="video">
          <template #tip><div>支持mp4/mov格式，视频大小不超过100M</div></template>
        </Upload>
      </el-form-item>
    </template>

    <!-- 文件 -->
    <template v-else-if="type === dictMsgType.file.type">
      <el-form-item label="上传文件" prop="fileUrl">
        <Upload v-model:fileUrl="form.fileUrl" type="file">
          <template #tip><div>支持pdf/ppt/word文件，文件大小不超过50M</div></template>
        </Upload>
      </el-form-item>
    </template>
  </el-form>
</template>

<script>
import { dictMsgType } from '@/utils/index'
var validateHtml = (rule, value, callback) => {
  if (/\.html$/gi.test(value)) {
    callback()
  } else {
    callback(new Error('必须以 .html 作为后缀'))
  }
}
var validateHttp = (rule, value, callback) => {
  if (/^https?:\/\//gi.test(value)) {
    callback()
  } else {
    callback(new Error('必须以 http://或 https://开头'))
  }
}
export default {
  components: {},
  props: {
    form: {
      type: Object,
      default: () => ({}),
    },
    type: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      dictMsgType,
      // 表单校验
      rules: Object.freeze({
        title: [{ required: true, message: '必填项', trigger: 'blur' }],
        picUrl: [{ required: true, message: '必填项', trigger: 'change' }],
        videoUrl: [{ required: true, message: '必填项', trigger: 'change' }],
        fileUrl: [{ required: true, message: '必填项', trigger: 'change' }],
        appid: [{ required: true, message: '必填项', trigger: 'blur' }],
        page: [
          { required: true, message: '必填项', trigger: 'blur' },
          { validator: validateHtml, trigger: 'blur' },
        ],
        url: [
          { required: true, message: '必填项', trigger: 'blur' },
          { validator: validateHttp, trigger: 'blur' },
        ],
      }),
    }
  },

  watch: {},
  computed: {
    // data() {
    //   return this.form
    // },
  },
  methods: {
    submit() {
      return this.$refs['form']
        .validate()
        .then(() => {
          return JSON.parse(JSON.stringify(this.form))
        })
        .catch((err) => {
          this.msgError(dictMsgType[this.type].name + '表单信息填写不完整')
          return Promise.reject(err)
        })
    },
  },
}
</script>

<style lang="scss" scoped></style>
