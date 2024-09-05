<script>
import { upload } from '@/api/common'
import { dateFormat, uuid } from '@/utils/common'

export default {
  components: {},
  emits: ['update:fileUrl', 'update:fileName', 'update:fileList', 'loadingChange'],
  props: {
    // 单文件上传时使用
    fileUrl: {
      type: String,
      default: '',
    },
    fileName: {
      type: String,
      default: '',
    },

    // 多文件上传时使用，例如: [{name: 'food.jpg', url: 'https://xxx.cdn.com/xxx.jpg'}]
    fileList: {
      type: Array,
      default: () => [],
    },

    type: {
      type: String,
      default: 'image',
      validator: function (value) {
        // 这个值必须匹配下列字符串中的一个
        return ['image', 'video', 'file'].includes(value)
      },
    },
    // 上传文件大小不能超过 maxSize MB, 各类型有默认限制 参见: maxSizeDefault
    maxSize: {
      type: [Number, String],
      default: undefined,
    },
    // 图片的宽高像素限制 [width(number), height(number)],默认null不限制,() => [1024, 1024]
    maxImgPx: {
      type: Array,
      default: null, // () => [100, 100]
    },
    // 允许上传的文件格式后缀名 eg:["jpg", "png"]，['*']为不限制，各类型有默认限制 参见: formatDefault
    format: {
      type: Array,
      default: undefined,
    },
    // 选择上传文件类型, 用于过滤系统选择文件类型, 默认根据类型自动匹配：见 acceptAuto，
    accept: {
      type: String,
      default: '',
    },
    // 是否能上传多个
    multiple: {
      type: Boolean,
      default: false,
    },
    // multiple为true时有效，最多上传几个,默认不限制，最小为1
    limit: {
      type: [Number, String],
      default: undefined,
    },
  },
  data() {
    return {
      loading: false,

      fileUrlShow: '',
      fileNameShow: '',
      fileListShow: '',

      file: undefined,
      speed: 0, // 上传网速
      percentage: 0, //上传进度
    }
  },
  watch: {
    fileUrl: {
      immediate: true,
      deep: true,
      handler(val) {
        this.fileUrlShow = val
      },
    },
    fileName: {
      immediate: true,
      deep: true,
      handler(val) {
        this.fileNameShow = val
      },
    },
    fileList: {
      immediate: true,
      deep: true,
      handler(val) {
        this.fileListShow = val
      },
    },
    loading: {
      immediate: true,
      deep: true,
      handler(val) {
        this.$emit('loadingChange', val)
      },
    },
  },
  computed: {
    // 识别选择文件类型
    acceptAuto() {
      return { image: 'image/*', video: 'video/*' }[this.type]
    },
  },
  created() {},
  mounted() {},
  methods: {
    upload() {
      this.loading = true
      let file = undefined
      if (!this.multiple || this.limit == 1) {
        file = this.file
      } else {
        // 多选上传是多次调用单传的
        file = this.file.shift()
      }
      let formData = new FormData()
      formData.append('file', file)
      upload(formData).then((dd) => {
        if (dd.code == 200) {
          let location = dd.data
          this.loading = false
          let url = window.URL.createObjectURL(file)

          let name = file.name
          if (!this.multiple) {
            this.fileUrlShow = url
            this.$emit('update:fileUrl', location)
            this.$emit('update:fileName', (this.fileNameShow = name))
          } else {
            this.fileListShow = this.fileListShow.concat({ name, url })
            this.$emit('update:fileList', this.fileList.concat({ name, url: location }))
          }
        } else {
          this.loading = false
          this.$message.error('上传失败，请稍后再试')
        }
      })
    },
    remove(i) {
      this.$confirm().then(() => {
        let clone = JSON.parse(JSON.stringify(this.fileList))
        this.fileListShow.splice(i, 1)
        clone.splice(i, 1)
        this.$emit('update:fileList', clone)
      })
    },
    handleExceed(file, fileList) {
      this.$message.error('最多上传' + this.limit + '张')
      this.loading = false
    },
    async handleBeforeUpload(file, filelist) {
      this.loading = true
      let isFormat = true
      let isSize = true

      // 统一校验文件后缀名格式
      // 如果没有显示配置 format 格式限制，则使用默认下面校验
      let format = this.format
      let tip = ''
      if (!format || !format.length) {
        let formatDefault = {
          image: { tip: 'png/jpg', value: ['png', 'jpg', 'jpeg'] },
          video: { value: ['mp4'] },
          file: { tip: 'word/pdf/ppt', value: ['doc', 'docx', 'pdf', 'ppt', 'pptx', 'pps', 'pptsx'] },
        }
        format = formatDefault[this.type].value
        tip = formatDefault[this.type].tip
      }

      const ext = file.name.split('.').pop()
      isFormat = format[0] === '*' || format.includes(ext)
      if (!isFormat) {
        this.$message.error('文件格式错误，仅支持 ' + (tip || format.join('，')) + ' 格式!')
        this.loading = false
        return Promise.reject()
      }

      // 统一校验文件体积
      // 如果没有显式配置 maxSize 限制大小，则使用下面默认值，单位 MB，
      let maxSize = this.maxSize
      if (!maxSize) {
        let maxSizeDefault = { image: 2, video: 100, file: 50 }
        maxSize || (maxSize = maxSizeDefault[this.type])
      }
      isSize = file.size / 1024 / 1024 < maxSize
      if (!isSize) {
        this.$message.error('上传文件大小不能超过 ' + maxSize + 'MB!')
        this.loading = false
        return Promise.reject()
      }

      // 各类型独有的校验
      let validate = true
      if (this.type === 'image') {
        // 图片
        let maxImgPx = this.maxImgPx
        if (maxImgPx) {
          try {
            await new Promise((resolve) => {
              let width, height
              let image = new Image()
              //加载图片获取图片真实宽度和高度
              image.onload = () => {
                width = image.width
                height = image.height
                if (width > maxImgPx[0]) {
                  validate = false
                  this.$message.error(`图片“宽”度超过${maxImgPx[0]}像素，请重新选择`)
                } else if (height > maxImgPx[1]) {
                  this.$message.error(`图片“高”度超过${maxImgPx[1]}像素，请重新选择`)
                  validate = false
                }
                window.URL && window.URL.revokeObjectURL(image.src)
                resolve()
              }
              if (window.URL) {
                let url = window.URL.createObjectURL(file)
                image.src = url
              } else if (window.FileReader) {
                let reader = new FileReader()
                reader.onload = function (e) {
                  let data = e.target.result
                  image.src = data
                }
                reader.readAsDataURL(file)
              }
            })
          } catch (e) {
            console.error(e)
          }
        }
      }
      if (!validate) {
        this.loading = false
      }
      // if (beforeUpload) {
      //   return beforeUpload(file)
      // }
      else if (!this.multiple || this.limit == 1) {
        this.file = file
      } else {
        Array.isArray(this.file) || (this.file = []) // 多选
        this.file.push(file)
      }
      return validate || Promise.reject()
    },
    onError(err, file, fileList) {
      this.loading = false
      this.$message.error('上传文件失败')
    },
    showView(index) {
      let imager = index !== undefined ? this.$refs.image[index] : this.$refs.image
      imager.$el.firstElementChild.click()
      // this.$nextTick(() => {
      //   // 为遮罩层添加关闭事件
      //   let maskEl = imager.$children[0].$refs['el-image-viewer__wrapper'].firstChild
      //   maskEl.addEventListener('click', () => {
      //     event.stopPropagation()
      //     imager.closeViewer()
      //   })
      // })
      // this.$refs.image.$refs.closeViewer();
    },
  },
}
</script>

<template>
  <div>
    <!-- 多个上传文件列表展示 -->
    <template v-if="multiple">
      <!-- 图片 -->
      <template v-if="type == 'image'">
        <!-- <transition-group> -->
        <div v-for="(item, index) in fileListShow" :key="index" class="img-item upload-item">
          <el-image
            ref="image"
            class="upload-img uploader-size"
            :src="item.url"
            fit="contain"
            :preview-src-list="fileListShow.map((e) => e.url)"
            alt="" />
          <div class="action-mask">
            <el-icon-search class="el-icon-search mr5" @click="showView(index)"></el-icon-search>
            <!-- <span v-if="action.includes('download')" @click="download(item)">
              <el-icon-download class="el-icon-download"></el-icon-download>
            </span> -->
            <el-icon-delete class="el-icon-delete mr5" @click="remove(index)"></el-icon-delete>
          </div>
        </div>
        <!-- </transition-group> -->
      </template>
      <!-- 后续再这里扩展其他文件列表 -->
    </template>

    <el-upload
      v-if="!multiple || !limit || fileListShow.length < limit"
      class="uploader"
      action="/api"
      :accept="accept || acceptAuto"
      :http-request="upload"
      :data="{ mediaType: type }"
      :show-file-list="false"
      :file-list="fileListShow"
      :disabled="loading"
      :multiple="multiple && limit != 1"
      :limit="limit"
      :on-error="onError"
      :on-exceed="handleExceed"
      :before-upload="handleBeforeUpload">
      <!--
      element-loading-text="正在上传..."
        :on-success="onSuccess"
       -->

      <slot>
        <div v-if="!loading && !fileUrlShow" class="uploader-icon upload-action uploader-size">
          <el-icon-plus class="el-icon-plus" />
        </div>

        <TransitionGroup>
          <!-- 上传进度条 -->
          <div class="upload-action uploader-size" v-if="loading" :key="1">
            <el-progress v-if="percentage" class="progress cc" type="circle" :percentage="percentage"></el-progress>
            <div class="el-loading-spinner">
              <svg viewBox="25 25 50 50" class="circular">
                <circle cx="50" cy="50" r="20" fill="none" class="path"></circle>
              </svg>
            </div>
            <div class="cc" style="margin-top: 35px" v-if="speed">
              {{ speed + 'M/s' }}
            </div>
          </div>

          <!-- 单文件上传的文件展示 -->
          <div v-if="!loading && fileUrlShow && !multiple" class="upload-item" :key="2">
            <template v-if="type === 'image'">
              <el-image
                v-if="fileUrlShow"
                ref="image"
                :src="fileUrlShow"
                class="upload-img upload-img-single uploader-size"
                :preview-src-list="[fileUrlShow]"
                preview-teleported
                fit="contain"
                @click.stop />

              <div class="action-mask" @click.self.stop>
                <el-icon-search class="el-icon-search" @click.stop="showView()"></el-icon-search>
                <el-icon-EditPen class="el-icon-EditPen"></el-icon-EditPen>
                <!-- <span v-if="action.includes('download')" @click.prevent.stop="download(item)">
              <el-icon-download class="el-icon-download"></el-icon-download>
            </span> -->
                <!-- <span @click.prevent.stop="remove()">
                  <el-icon-delete class="el-icon-delete"></el-icon-delete>
                </span> -->
              </div>
            </template>
            <template v-else-if="type === 'video'">
              <video
                ref="video"
                id="myVideo"
                class="upload-video"
                width="100%"
                controls
                webkit-playsinline="true"
                playsinline="true"
                :autoplay="false"
                :key="fileUrlShow"
                preload="auto">
                <source :src="fileUrlShow" type="video/mp4" />
              </video>
              <div class="action-mask" style="height: 30%" @click.self.stop>
                <el-icon-EditPen class="el-icon-EditPen"></el-icon-EditPen>
              </div>
            </template>
            <template v-else class="al">
              {{ fileNameShow || fileUrlShow }}
              <el-icon-EditPen class="el-icon-EditPen ml10"></el-icon-EditPen>
              <!-- a链接用本地视频打不开，视频地址使用远程地址 -->
              <a @click.stop :href="/\.mp4$/.test(fileNameShow) ? fileUrl : fileUrlShow" target="_blank">
                <el-icon-view class="el-icon-view ml10" style="vertical-align: middle"></el-icon-view>
              </a>
            </template>
          </div>
        </TransitionGroup>
      </slot>
    </el-upload>

    <!-- 上传格式，大小等提示语 -->
    <div class="tip">
      <slot name="tip"></slot>
    </div>
  </div>
</template>

<style lang="scss" scoped>
::v-deep.uploader {
  display: inline-block;
  vertical-align: middle;
  .el-upload {
    display: block;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
}
// 默认组件大小，如需修改，请通过外部重写该类样式
.uploader-size {
  width: 178px;
  height: 178px;
}
.upload-action {
  position: relative;
  text-align: center;
}
.uploader-icon {
  display: flex;
  font-size: 28px;
  align-items: center;
  justify-content: center;
  color: var(--font-black-6);
  border-radius: 6px;
  border: 1px dashed var(--border-black-10);
  transition: all 0.3s;
  &:hover {
    border-color: var(--color);
    color: var(--color);
  }
}
.progress {
  overflow: hidden;
}
.upload-img-single {
  border: 1px dashed var(--border-black-9);
}
.upload-img {
  display: block;
}
.upload-video {
  display: block;
  width: 300px;
  height: 150px;
  box-sizing: border-box;
  color: var(--font-white, #fff);
  background-color: var(--bg-black);
  position: relative;
  padding: 0;
  font-size: 10px;
  line-height: 1;
  font-weight: normal;
  font-style: normal;
  word-break: initial;
}
.tip {
  color: var(--font-black-7);
  font-size: 12px;
  line-height: 1.5;
  margin-top: 5px;
}
.img-item {
  position: relative;
  display: inline-block;
  vertical-align: middle;
  margin: 0 10px 10px 0;
  transition: all 0.3s;
}
.action-mask {
  position: absolute;
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 0 30px;
  width: 100%;
  height: 100%;
  left: 0;
  top: 0;
  cursor: pointer;
  text-align: center;
  color: var(--font-white, #fff);
  opacity: 0;
  font-size: 20px;
  transition: opacity 0.3s;
  background-color: var(--bg-black-5);
  z-index: 1;
}
.upload-item {
  &:hover {
    .action-mask {
      opacity: 1;
    }
  }
}
</style>
