<!-- // 带 插入 表情/客户昵称 等扩展工具栏的文本内容框 -->
<script>
export default {
  name: '',
  inheritAttrs: false,
  props: {
    modelValue: '',
    // ['emoji','insertCustomerNickName']
    toolbar: {
      type: Array,
      default: () => ['emoji'],
    },
    nikeName: {
      type: String,
      default: '$客户昵称$',
    },
  },
  components: {
    SelectEmoji: defineAsyncComponent(() => import('@/components/SelectEmoji')),
  },
  data() {
    return {}
  },
  computed: {
    proxyValue: {
      get() {
        return this.modelValue
      },
      set(val) {
        this.$emit('update:modelValue', val)
      },
    },
  },
  watch: {},
  created() {},
  mounted() {},
  methods: {
    // 插入内容
    insertContent(value) {
      const $textarea = this.$refs.textarea.$el.children[0]
      const content = this.proxyValue || ''
      const textIndex = $textarea.selectionStart
      this.proxyValue = content.slice(0, textIndex) + value + content.slice(textIndex)
    },
  },
}
</script>

<template>
  <div>
    <el-input ref="textarea" v-model="proxyValue" type="textarea" v-bind="$attrs"></el-input>
    <div class="mt10">
      <SelectEmoji v-if="toolbar.includes('emoji')" @select="insertContent">
        <template v-slot:default="{ emojisArr }">
          <el-button>
            {{ emojisArr[0].emoji }}
          </el-button>
        </template>
      </SelectEmoji>
      <el-button class="ml10" v-if="toolbar.includes('insertCustomerNickName')" @click="insertContent(nikeName)">
        插入客户昵称
      </el-button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
textarea {
  white-space: pre-wrap; //保留所有空格
}
.custom-ai {
  border: 1px solid var(--color);
  color: var(--color);
  font-weight: 600;
}
</style>
