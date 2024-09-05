<template>
  <div
    class="pagination-container"
    ref="pagination"
    v-if="['false'].includes($attrs['hide-on-single-page'] + '') || $attrs.total > limit">
    <el-pagination
      v-bind="Object.assign({}, this.$attrs, { style: undefined, class: undefined })"
      :background="background"
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :layout="layout"
      :page-sizes="pageSizes"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange" />
  </div>
</template>

<script>
// import { scrollTo } from '@/utils/scroll-to'

export default {
  name: 'Pagination',
  props: {
    page: {
      type: Number,
      default: 1,
    },
    limit: {
      type: Number,
      default: 10,
    },
    pageSizes: {
      type: Array,
      default() {
        return [10, 20, 50, 100, 500]
      },
    },
    layout: {
      type: String,
      default: 'total, sizes, prev, pager, next, jumper',
    },
    background: {
      type: Boolean,
      default: true,
    },
    autoScroll: {
      type: Boolean,
      default: true,
    },
    scrollElement: {
      type: Object,
    },
  },
  emits: ['update:page', 'update:limit', 'pagination'],
  computed: {
    currentPage: {
      get() {
        return this.page
      },
      set(val) {
        this.$emit('update:page', val)
      },
    },
    pageSize: {
      get() {
        return this.limit
      },
      set(val) {
        this.$emit('update:limit', val)
      },
    },
  },
  methods: {
    handleSizeChange(val) {
      this.$emit('pagination', { page: this.currentPage, limit: val })
      if (this.autoScroll) {
        this.scrollTo()
      }
    },
    handleCurrentChange(val) {
      this.$emit('pagination', { page: val, limit: this.pageSize })
      if (this.autoScroll) {
        this.scrollTo()
      }
    },
    scrollTo() {
      setTimeout(() => {
        this.$refs.pagination.parentNode.scrollTop = 0
        document.querySelector('#page-container').scrollTop = 0
      }, 200)
    },
  },
}
</script>

<style scoped>
.pagination-container {
  display: flex;
  justify-content: right;
  position: sticky;
  bottom: 0;
  z-index: 9;
  text-align: right;
  background: var(--bg-white);
  padding: 15px 5px 20px;
  border-top: 1px solid var(--border-black-11);
}
/* .pagination-container.hidden {
  display: none;
} */
.g-card > .pagination-container {
  margin-bottom: -20px;
  bottom: -20px;
}
</style>
