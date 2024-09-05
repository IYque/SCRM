<script>
import { dictMsgType } from '@/utils/index'
export default {
  components: {},
  props: {
    // 消息列表
    // 示例：[{ text: form.welcomeMsg || '请输入加群引导语' },{ title: form.linkTitle, desc: form.linkDesc, image: form.linkCoverUrl },]
    list: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return { dictMsgType }
  },
  watch: {},
  computed: {},
  created() {},
  mounted() {},
  methods: {
    transType(fileName) {
      if (!fileName) return

      const ext = fileName.split('.').pop()
      if (ext === 'pdf') {
        return 'pdf'
      } else if (['doc', 'docx'].includes(ext)) {
        return 'word'
      } else if (['ppt', 'pptx', 'pps', 'pptsx'].includes(ext)) {
        return 'ppt'
      } else {
        return ''
      }
    },
  },
}
</script>

<template>
  <PhoneInterface class="PhoneChatList">
    <div class="time">20:24</div>

    <ul class="msg-ul">
      <li class="flex msg-li" v-if="$slots.default && Array.isArray($slots.default()[0].children)">
        <div class="avatar fxnone"><svg-icon icon="user" /></div>

        <div class="msg">
          <slot />
        </div>
      </li>

      <template v-for="(item, index) in list" :key="index">
        <li class="flex msg-li" :let="(data = item[item.msgtype] || {})">
          <div class="avatar fxnone"><svg-icon icon="user" /></div>

          <div class="msg">
            <!-- 文字 -->
            <span class="msg-text" v-if="item.text">{{ item.text }}</span>

            <!-- 链接 -->
            <slot
              v-else-if="item.msgtype === dictMsgType.link.type || item.msgtype === dictMsgType.miniprogram.type"
              name="imageText"
              :msg="item">
              <div class="msg-image-text">
                <div class="image-text-title">{{ data.title }}</div>
                <div class="image-text-content">
                  <div class="image-text-desc" v-if="data.desc">{{ data.desc }}</div>
                  <img :src="data.picUrl" class="image-text-image fxnone" />
                </div>
              </div>
            </slot>

            <!-- 图片 -->
            <img class="msg-image" v-else-if="item.msgtype === dictMsgType.image.type" :src="data.picUrl" />

            <!-- 视频 -->
            <video
              v-else-if="item.msgtype === dictMsgType.video.type"
              id="video"
              class="video-js vjs-default-skin vjs-big-play-centered"
              controls
              webkit-playsinline="true"
              playsinline="true"
              :autoplay="false"
              preload="none"
              style="height: 100%; width: 100%">
              <source :src="data.videoUrl" type="video/mp4" />
            </video>

            <!-- 小程序 -->
            <!-- <div class="miniprogram" v-else-if="item.msgtype === dictMsgType.miniprogram.type">
              <div class="mini-header">
                {{ data.title }}
              </div>
              <img class="mini-img" :src="data.picurl" />
              <div class="mini-footer">小程序</div>
            </div> -->

            <!-- 文件 -->
            <div class="fxbw" v-else-if="item.msgtype === dictMsgType.file.type">
              <div>
                {{ data.fileUrl }}
              </div>
              <div class="fxnone" style="width: 100px">
                <svg-icon class="" :icon="transType(data.fileUrl)" v-if="data.fileUrl"></svg-icon>
              </div>
            </div>
          </div>
        </li>
      </template>
    </ul>
  </PhoneInterface>
</template>

<style lang="scss" scoped>
.time {
  font-size: 12px;
  color: var(--font-black-7);
  padding: 10px 0 5px;
  text-align: center;
}
.msg-ul {
  margin: 0;
  padding: 0;
  max-height: 80%;
  overflow: auto;
  padding: 0 10px;
}
.msg-li {
  margin-bottom: 10px;
}
.msg {
  min-height: 28px;
  background: var(--bg-white);
  border: 1px solid var(--border-black-9);
  display: inline-block;
  position: relative;
  text-align: left;
  font-size: 14px;
  line-height: 22px;
  padding: 6px 12px;
  border-radius: 4px;
  max-width: 80%;
  min-width: 24px;
  box-sizing: border-box;
  word-break: break-all;
  white-space: pre-line;
  margin-left: 10px;
  &:before {
    content: ' ';
    display: block;
    position: absolute;
    left: -6px;
    border-width: 4px 6px;
    border-left: 0;
    width: 0;
    border-style: solid;
    border-color: transparent;
    border-right-color: var(--border-white);
    top: 10px;
    z-index: 888;
  }
}
.avatar {
  border: 1px solid var(--border-white);
  background: var(--bg-white);
  border-radius: 6px;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.msg-image-text {
  .image-text-title {
    font-weight: 500;
  }
  .image-text-content {
    display: flex;
    justify-content: space-between;
    gap: 10px;
    margin-top: 2px;
  }
  .image-text-desc {
    font-size: 12px;
    color: var(--font-black-6);
  }
  .image-text-image {
    width: 50px;
    height: 50px;
    border-radius: 6px;
    border: 1px solid var(--border-black-11);
    object-fit: contain;
  }
}
</style>
